package com.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuditAuthenticationSuccessHandler auditAuthenticationSuccessHandler;
    private final AuditLogoutSuccessHandler auditLogoutSuccessHandler;

    public SecurityConfig(
        AuditAuthenticationSuccessHandler auditAuthenticationSuccessHandler,
        AuditLogoutSuccessHandler auditLogoutSuccessHandler
    ) {
        this.auditAuthenticationSuccessHandler = auditAuthenticationSuccessHandler;
        this.auditLogoutSuccessHandler = auditLogoutSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/dashboard").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/inventory", "/inventory/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/change-password").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/products", "/search").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/products/new", "/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/products", "/update-product", "/products/delete/**").hasRole("ADMIN")
                .requestMatchers("/delete/**", "/edit/**").hasRole("ADMIN")
                .requestMatchers("/categories/**").hasRole("ADMIN")
                .requestMatchers("/suppliers/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasRole("ADMIN")
                .requestMatchers("/requests/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/reports/**", "/sales/**").hasRole("ADMIN")
                .requestMatchers("/export", "/export-sales", "/export-stock", "/export-products", "/export-user-audit").hasRole("ADMIN")
                .requestMatchers("/low-stock", "/update-stock", "/increase-stock/**", "/decrease-stock/**", "/export-low-stock")
                    .hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("userId")
                .successHandler(auditAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessHandler(auditLogoutSuccessHandler)
                .permitAll()
            );

        return http.build();
    }
}
