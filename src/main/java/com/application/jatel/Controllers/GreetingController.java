package com.application.jatel.Controllers;

import com.application.jatel.Models.User;
import com.application.jatel.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {
    @Autowired
    UserRepository userRepository;
    @GetMapping("/homepage")
    public String homepage1(Model model,  @CurrentSecurityContext(expression = "authentication")
    Authentication authentication)
    {
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).get();
        model.addAttribute("AuthorizedUser", user);
        return "homepage";
    }
    @GetMapping("")
    public String homepage2(Model model,  @CurrentSecurityContext(expression = "authentication")
    Authentication authentication)
    {
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).get();
        model.addAttribute("AuthorizedUser", user);
        return "homepage";
    }
    @GetMapping("/startpage")
    public String startpage()
    {
        return "startpage";
    }
}