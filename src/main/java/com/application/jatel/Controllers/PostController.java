package com.application.jatel.Controllers;

import com.application.jatel.Models.Comment;
import com.application.jatel.Models.User;
import com.application.jatel.Repo.CommentRepository;
import com.application.jatel.Repo.PostRepository;
import com.application.jatel.Models.Post;
import com.application.jatel.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class PostController {
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
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @GetMapping("/addblog")
    public String NewBlog(Model model,
                          @CurrentSecurityContext(expression = "authentication")
                          Authentication authentication,
                          Post post)
    {
        String name = authentication.getName();
        User user1 = userRepository.findByUsername(name).get();
        model.addAttribute("AuthorizedUser", user1);
        model.addAttribute("post", post);
        return "addblog";
    }
    @PostMapping("/addblog")
    public String NewBlogAdd(@CurrentSecurityContext(expression = "authentication")
                             Authentication authentication,
                             @Valid Post post,
                             BindingResult bindingResult, Model model)
    {
        if (bindingResult.hasErrors())
        {
            model.addAttribute("MessageTitle", "The number of characters must be between 1 and 255");
            model.addAttribute("MessageText", "The number of characters must not betwin 1 and 10000");
            return "addblog";
        }
        String name = authentication.getName();
        User user1 = userRepository.findByUsername(name).get();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd" + " - " + "hh:mm");
        post.setDate(simpleDateFormat.format(date).toString());
        post.setAuthor(user1);
        postRepository.save(post);
        return "redirect:/blog";
    }
    @GetMapping("/blog/{id}")
    public String ReadBlog(@PathVariable(value = "id") long id, Model model,
                           @CurrentSecurityContext(expression = "authentication")
                           Authentication authentication,
                           Comment comment)
    {
        String name = authentication.getName();
        User authorized = userRepository.findByUsername(name).get();
        if(!postRepository.existsById(id))
        {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("posts", res);
        model.addAttribute("authorized", authorized);
        ArrayList<Comment> comments = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM COMMENT WHERE POST_ID=" + post.get().getId());
            ResultSet resultSet =preparedStatement.executeQuery();
            while (resultSet.next())
            {
                comments.add(commentRepository.findById(resultSet.getLong("id")).get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("comment", comment);
        model.addAttribute("comments", comments);
        model.addAttribute("AuthorizedUser", authorized);
        return "readblog";
    }
    @PostMapping("/blog/{id}")
    public String PostComment(@PathVariable(value = "id") long id,
                              @CurrentSecurityContext(expression = "authentication")
                              Authentication authentication,
                              @RequestParam String commentText,
                              Model model)
    {
        String name = authentication.getName();
        User authorized = userRepository.findByUsername(name).get();
        Post post = postRepository.findById(id).get();
        Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setPost(post);
        comment.setAuthor(authorized);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd" + " - " + "hh:mm");
        comment.setDateComment(simpleDateFormat.format((date)).toString());
        commentRepository.save(comment);
        model.addAttribute("AuthorizedUser", authorized);
        return "redirect:/blog/{id}";
    }
    @PostMapping("/blog/{id}/deletecomment{idcomment}")
    public String DeleteComment(@PathVariable(value = "idcomment") long idcomment)
    {
        Optional<Comment> comment = commentRepository.findById(idcomment);
        commentRepository.delete(comment.get());
        return "redirect:/blog/{id}";
    }
    @GetMapping("/aboutJatel")
    public String aboutJatel(Model model, @CurrentSecurityContext(expression = "authentication")
    Authentication authentication)
    {
        String name = authentication.getName();
        User user1 = userRepository.findByUsername(name).get();
        model.addAttribute("AuthorizedUser", user1);
        return "aboutJatel";
    }
    @GetMapping("/blog/{id}/edit")
    public String EditBlog(@PathVariable(value = "id") long id, Model model,
                           @CurrentSecurityContext(expression = "authentication")
                           Authentication authentication)
    {
        if(!postRepository.existsById(id))
        {
            return "redirect:/blog";
        }
        String name = authentication.getName();
        User authorized = userRepository.findByUsername(name).get();
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("posts", res);
        model.addAttribute("AuthorizedUser", authorized);
        return "editblog";
    }
    @PostMapping("/blog/{id}/edit")
    public String PostEditedBlog(@PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String text, Model model)
    {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setText(text);
        postRepository.save(post);
        return "redirect:/blog";
    }
    @PostMapping("/blog/{id}/delete")
    public String DeleteBlog(@PathVariable(value = "id") long id)
    {
        Post post = postRepository.findById(id).orElseThrow();
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM COMMENT WHERE POST_ID=" + post.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        postRepository.delete(post);
        return "redirect:/blog";
    }
    @GetMapping("/author/{id}")
    public String Author(@PathVariable(value = "id") long id, Model model)
    {
        ArrayList<Post> res = new ArrayList<>();
        if(!userRepository.existsById(id)) return "redirect:/blog";
        User author = userRepository.findById(id).get();
        model.addAttribute("Author", author);
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM POST WHERE USER_ID=" + author.getId());
            ResultSet resultSet =preparedStatement.executeQuery();
            while (resultSet.next())
            {
                res.add(postRepository.findById(resultSet.getLong("id")).get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("postsOfAuthor", res);
        return "Author";
    }
    @GetMapping("/blog")
    public String blog(Model model, @CurrentSecurityContext(expression = "authentication")
    Authentication authentication)
    {
        String name = authentication.getName();
        User user1 = userRepository.findByUsername(name).get();
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("AuthorizedUser", user1);
        return "blog";
    }
}
