package com.application.jatel.Services;

import com.application.jatel.Models.SecurityUser;
import com.application.jatel.Repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsServise implements UserDetailsService {
    private final UserRepository userRepository;

    public JpaUserDetailsServise(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userRepository.findByUsername(username).
               map(SecurityUser::new)
               .orElseThrow(()->new UsernameNotFoundException("Username not found" + username));
    }
}
