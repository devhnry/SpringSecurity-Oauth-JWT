package com.dev.h3nry.service;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        /* Cast the Authentication object to OAuth2AuthenticationToken */
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        /* Retrieve the client registration ID (e.g., "gitlab", "github", "google") */
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

        /* Retrieve the OAuth2User */
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        /* Load the authorized client based on the client registration ID */
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistrationId,
                authentication.getName()
        );

        //Fetch the access token and refresh token (If Available) from the authorized client
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        log.info("Oauth Access token: {}", accessToken);
        String refreshToken = authorizedClient.getRefreshToken() != null ?
                authorizedClient.getRefreshToken().getTokenValue() : null;
        log.info("Oauth Refresh token: {}", refreshToken);

        /* Logic that handles checking the user saving the Token */

        /* Redirect or forward to a specific URL after successful authentication */
        response.sendRedirect("/api/v1/dashboard");

    }
}
