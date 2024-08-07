package com.dev.h3nry.config;

import com.dev.h3nry.service.LogOutHandler;
import com.dev.h3nry.service.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityAuthenticationProvider securityAuthenticationProvider;
    private final JwtSecurityFilter jwtSecurityFilter;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final LogOutHandler logOutHandler;

    /**
     * Security configuration for HTTP requests, session management, and login/logout handling.
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                /* Allow unrestricted access to certain endpoints */
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/github/login**","/gitlab/login**","/goggle/login**", "/error**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                /* Manage session creation policy for JWT-based authentication */
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(securityAuthenticationProvider.authenticationProvider())
                .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)

                /* Handles the logout logic for both JWT and OAuth */
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .addLogoutHandler(logOutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .build();
    }

    /** Provides the AuthenticationManager bean used for authentication. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
