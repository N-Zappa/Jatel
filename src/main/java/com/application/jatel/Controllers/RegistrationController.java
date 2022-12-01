package com.application.jatel.Controllers;

import com.application.jatel.Models.Post;
import com.application.jatel.Models.User;
import com.application.jatel.Repo.PostRepository;
import com.application.jatel.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;

@Controller
public class RegistrationController {
    private static final String URL = "jdbc:mysql://localhost:3306/jateldb";
    private static String USERNAME = "root";
    private static String PASSWORD = "1DjJkd/nz?-h";
    private static Connection connection;
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PasswordEncoder encoder;
    @GetMapping("/registration")
    public String RegistrationGet()
    {
        return "registration";
    }
    @PostMapping("/registration")
    public String RegistrationPost(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String nameOfUser,
                                   Model model)
    {
        String result = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM USER WHERE USERNAME=" + "'"+username+"'");
             ResultSet resultSet = preparedStatement.executeQuery();
                 while (resultSet.next())
                 {
                     result=resultSet.getString("username");
                 }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(result==null)
        {
                User user = new User(username, encoder.encode(password), "USER_ROLE");
                user.setNameOfUser(nameOfUser);
                userRepository.save(user);
                return "redirect:/login";
        }
        else
        model.addAttribute("Message","You cannot create two accounts for one email");
        return "registration";
    }
    @GetMapping("/login")
    public String Login()
    {
        return "login";
    }
    @PostMapping("/login")
    public String Enter()
    {
        return "redirect:/homepage";
    }
    @GetMapping("/person/{id}/profile")
    public String ProfileOfUser(@PathVariable(value = "id") long id, Model model)
    {
        ArrayList<Post> res = new ArrayList<>();
        User authorized = userRepository.findById(id).get();
        model.addAttribute("AuthorizedUser", authorized);
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM POST WHERE USER_ID=" + authorized.getId());
            ResultSet resultSet =preparedStatement.executeQuery();
            while (resultSet.next())
            {
                res.add(postRepository.findById(resultSet.getLong("id")).get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("postsOfAuthorizedUser", res);
        return "profile";
    }
    @GetMapping("/login/error")
    public String LoginError(Model model)
    {
        model.addAttribute("error", "Wrong password or login");
        return "login";
    }
    @PostMapping("/login/error")
    public String LoginErrorPost()
    {
        return "redirect:/homepage";
    }
}
