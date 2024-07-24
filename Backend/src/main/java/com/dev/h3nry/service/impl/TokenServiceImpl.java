package com.dev.h3nry.service.impl;

import com.dev.h3nry.entity.AuthToken;
import com.dev.h3nry.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service @Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Called by the Oauth2SuccessHandler to Generate AuthToken from the Authorized Client
     *
     * @param authentication The Authentication object containing user details.
     * @param clientRegistrationId The String containing the ClientRegistrationId (GITHUB, GOOGLE, GITLAB)
     */
    @Override
    public AuthToken handleInitialAuthenticationSuccess(Authentication authentication, String clientRegistrationId) {
        log.info("Authorized client registration id {}", clientRegistrationId);

        // Get Authorised Client from OAuth
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistrationId, authentication.getName()
        );
        log.info("Authorized Client: {}", authentication.getName());

        // Generate accessToken and RefreshToken.
        AuthToken authToken = generateAndSaveNewAccessTokenAndRefreshToken(authorizedClient);
        log.info("Oauth token: {}", authToken.getRefreshToken());

        return authToken;
    }

    /**
     * Takes in the AuthorizedClient Upon SignUp and Generates the Necessary Tokens (Access and Refresh Token)
     *
     * @param authorizedClient The AuthorizedClient along with its info.
    */
    protected AuthToken generateAndSaveNewAccessTokenAndRefreshToken(OAuth2AuthorizedClient authorizedClient) {
        log.info("Creating access token for user");
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                authorizedClient.getAccessToken().getTokenValue(),
                Instant.now(), // Time the token was created
                Instant.now().plus(24, ChronoUnit.HOURS) // Expires every 24 hours it is created
        );
        log.info("Oauth Access token: {}", accessToken.getTokenValue());

        log.info("Creating refresh token for user");
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                authorizedClient.getRefreshToken().getTokenValue(),
                null
        );
        log.info("Oauth Refresh token: {}", refreshToken.getTokenValue());

        /*
           Creates a new AuthToken that will be mapped to the user.
           Takes in the generated tokens, will be mapped to the OauthUser in 'Oauth2Success Handler'
        */
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(accessToken.getTokenValue());
        authToken.setExpiresAt(accessToken.getExpiresAt());
        authToken.setIssuedAt(accessToken.getIssuedAt());
        authToken.setRefreshToken(refreshToken.getTokenValue());
        log.info("Token has been created successfully");

        return authToken;
    }

}
