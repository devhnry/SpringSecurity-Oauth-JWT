package com.dev.h3nry.config;

import com.dev.h3nry.service.AppUserDetailsService;
import com.dev.h3nry.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Filter each request to check for a valid JWT token in the Authorization header.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        /* Retrieve the Authorization header from the request */
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userEmail = null;

        /* Extract the JWT token and user email if the Authorization header is present and starts with "Bearer " */
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            userEmail = jwtService.extractUsername(jwtToken);
        }

        /* Check if the user email is not null and the current SecurityContext does not have an authenticated user */
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            /* Load user details from the user service */
            UserDetails currentUser = userDetailsService.loadUserByUsername(userEmail);

            /* Validate the JWT token */
            if (jwtService.isTokenValid(jwtToken, currentUser)) {
                /* Create an authentication token and set the authentication details */
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                /* Set the authentication in the SecurityContext */
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        /* Continue the filter chain */
        filterChain.doFilter(request, response);
    }
}
