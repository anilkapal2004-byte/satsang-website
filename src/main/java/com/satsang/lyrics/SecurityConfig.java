package com.satsang.lyrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // NOTE: This app does not use Spring Security's own authentication —
        // admin routes are protected manually via HttpSession checks inside
        // AdminController (see the "UNAUTHORIZED" guards). Because of that,
        // every route is permitAll() here; Spring Security is only being used
        // for CSRF handling and the password encoder bean. This means EVERY
        // write endpoint in AdminController MUST have its own session check —
        // there is no other enforcement layer. If you add new admin endpoints,
        // don't forget the guard.
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable());

        return http.build();
    }
}


