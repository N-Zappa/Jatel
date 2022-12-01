package com.application.jatel;

import com.application.jatel.Models.Post;
import com.application.jatel.Models.User;
import com.application.jatel.Repo.PostRepository;
import com.application.jatel.Repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JatelApplication {

    public static void main(String[] args) {
        SpringApplication.run(JatelApplication.class, args);
    }
    }
