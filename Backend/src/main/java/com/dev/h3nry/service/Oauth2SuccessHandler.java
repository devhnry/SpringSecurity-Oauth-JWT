package com.dev.h3nry.service;

import com.dev.h3nry.entity.AppUser;
import com.dev.h3nry.entity.AuthToken;
import com.dev.h3nry.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    /**
     * Called when an OAuth2 authentication is successful.
     *
     * @param request The HttpServletRequest.
     * @param response The HttpServletResponse.
     * @param authentication The Authentication object containing user details.
     * @throws IOException If an input or output exception occurs.
     * @throws ServletException If a servlet-specific exception occurs.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        /* Cast the Authentication object to OAuth2AuthenticationToken */
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        /* Retrieve the client registration ID (e.g., "gitlab", "github", "google") */
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

        /* Gets the OauthUser from Authentication */
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        /* Handle the successful authentication and generate auth token for use */
        Optional<AppUser> existingUser = userRepository.findAppUserByEmail(oAuth2User.getAttribute("email"));
        if (existingUser.isEmpty()) {

            /* Handles the Logic for Initial Signup... */
            AuthToken authToken = tokenService.handleInitialAuthenticationSuccess(authentication, clientRegistrationId);
            log.info("Generated AuthToken {}", authToken);

            /* Should "redirect" them here after Successful Signup ONLY ( nothing else ) */
            response.sendRedirect("/success");

        }else{
            /* Redirect or forward to a specific URL after successful authentication */
            response.sendRedirect("/dashboard");
        }

    }
}
