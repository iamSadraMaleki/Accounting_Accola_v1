package com.example.project.signup;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthTokenFilter jwtAuthTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        System.out.println(">>> [SecurityConfig] Creating PasswordEncoder Bean...");
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) { // <<< PasswordEncoder اینجا تزریق میشه
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        System.out.println(">>> [SecurityConfig] Creating DaoAuthenticationProvider Bean...");
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        System.out.println(">>> [SecurityConfig] Creating AuthenticationManager Bean...");
        return authConfig.getAuthenticationManager();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        System.out.println(">>> [SecurityConfig] Creating CorsConfigurationSource Bean...");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173","http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control", "X-Requested-With", "Accept", "Origin"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthTokenFilter jwtAuthTokenFilter,
                                           DaoAuthenticationProvider daoAuthenticationProvider,
                                           JwtAuthEntryPoint unauthorizedHandler
            /* CorsConfigurationSource corsConfigSource */) throws Exception {
        System.out.println(">>> [SecurityConfig] Configuring SecurityFilterChain Bean (SECURITY DISABLED - Permit All!)"); // لاگ برای هشدار

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        return http.build();
    }
}