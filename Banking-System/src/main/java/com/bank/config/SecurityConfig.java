package com.bank.config;

import com.bank.service.JwtService;
import com.bank.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public OncePerRequestFilter jwtAuthFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException,
                    IOException {

                String path =
                    request.getRequestURI();

                // Allow these without token
                if (path.endsWith(".html") ||
                    path.equals("/") ||
                    path.startsWith("/css/") ||
                    path.startsWith("/js/") ||
                    path.startsWith("/images/") ||
                    path.startsWith("/api/auth/")) {
                    filterChain.doFilter(
                        request, response);
                    return;
                }

                String authHeader =
                    request.getHeader("Authorization");
                String token = null;
                String username = null;

                if (authHeader != null &&
                        authHeader.startsWith(
                            "Bearer ")) {
                    token = authHeader.substring(7);
                    try {
                        username = jwtService
                            .extractUsername(token);
                    } catch (Exception e) {
                        // Invalid token - skip
                    }
                }

                if (username != null &&
                        SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {
                    UserDetails userDetails =
                        userDetailsService
                        .loadUserByUsername(username);
                    if (jwtService.validateToken(
                            token, userDetails)) {
                        UsernamePasswordAuthenticationToken
                            authToken =
                            new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails
                                .getAuthorities());
                        SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                    }
                }
                filterChain.doFilter(
                    request, response);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/register.html",
                    "/dashboard.html",
                    "/admin.html",
                    "/forgot-password.html",
                    "/api/auth/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                jwtAuthFilter(),
                UsernamePasswordAuthenticationFilter
                .class);

        return http.build();
    }
}