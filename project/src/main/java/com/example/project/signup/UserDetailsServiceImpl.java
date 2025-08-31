package com.example.project.signup;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);


    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found during loadUserByUsername: {}", username);
                    return new UsernameNotFoundException("کاربری با نام کاربری پیدا نشد: " + username);
                });


        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null && !user.getRole().isEmpty()) {

            String roleWithPrefix = "ROLE_" + user.getRole().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
            log.debug("Assigned authority {} to user {}", roleWithPrefix, username);
            // ------------------------------------
        } else {

            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.warn("User {} has no role assigned, defaulting to ROLE_USER", username);
        }


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities

        );
    }
}