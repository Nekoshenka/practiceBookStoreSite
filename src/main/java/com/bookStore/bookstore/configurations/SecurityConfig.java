package com.bookStore.bookstore.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Доступ гостя
                        .requestMatchers("/", "/books", "/books/{id}", "/css/**", "/js/**", "/images/**", "/login", "/register", "/h2-console/**", "/uploads/**").permitAll()

                        // Доступ ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Доступ USER
                        .requestMatchers("/cart/**").hasRole("USER")

                        // Доступ EMPLOYEE
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/books/new", "/books/edit/**", "/books/delete/**").hasRole("EMPLOYEE")

                        // Остальное только с авторизацией
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        http.headers().frameOptions().disable();
        http.csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
