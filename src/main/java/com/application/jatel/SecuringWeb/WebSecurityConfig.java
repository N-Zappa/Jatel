package com.application.jatel.SecuringWeb;

import com.application.jatel.Services.JpaUserDetailsServise;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JpaUserDetailsServise jpaUserDetailsServise;

    public WebSecurityConfig(JpaUserDetailsServise jpaUserDetailsServise) {
        this.jpaUserDetailsServise = jpaUserDetailsServise;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests(auth -> auth
                        .mvcMatchers("/startpage").permitAll()
                        .mvcMatchers("/registration").permitAll()
                        .antMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .userDetailsService(jpaUserDetailsServise)
                .headers(headers->headers.frameOptions().sameOrigin())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login/error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/homepage")
                )
                .build();
    }
    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
