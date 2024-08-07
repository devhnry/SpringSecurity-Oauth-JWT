package com.dev.h3nry.service.impl;

import com.dev.h3nry.dto.AuthSuccessResponseDto;
import com.dev.h3nry.dto.DefaultResponseDto;
import com.dev.h3nry.service.JwtService;
import com.dev.h3nry.service.OAuth2HandlerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @Slf4j
@RequiredArgsConstructor
public class OAuth2HandlerServiceImpl implements OAuth2HandlerService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String GITHUB_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String GITHUB_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String REDIRECT_URI;

    private final String STATE = UUID.randomUUID().toString();

    @Override
    public void githubLogin(HttpServletResponse response) throws IOException {
        // List of params needed for GITLAB to perform the Request
        Map<String, String> body = new HashMap<>();
        body.put("client_id", GITHUB_CLIENT_ID);
        body.put("redirect_uri", REDIRECT_URI);
        body.put("state", STATE);
        body.put("scope", "user");
        /*
            Forms a String : -> parameters
            client_id=GITLAB_CLIENT_ID&redirect_uri=REDIRECT_URI&state=STATE&scope=USER
        */
        String form = body.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String REQUEST_URI = "https://github.com/login/oauth/authorize?" + form;
        log.info(REQUEST_URI);
        response.sendRedirect(REQUEST_URI);
    }

    @Override
    public DefaultResponseDto<AuthSuccessResponseDto> handleGithubRedirect(String code, String state, HttpServletResponse response)
            throws URISyntaxException, IOException, InterruptedException {
        DefaultResponseDto<AuthSuccessResponseDto> responseDto = new DefaultResponseDto<>();
        AuthSuccessResponseDto authSuccessResponseDto = new AuthSuccessResponseDto();

        if(state.equals(STATE)){
            log.info("Github redirect success: STATE MATCHES");
        }else {
            log.error("Github redirect success: SUSPICIOUS ACTIVITIES DETECTED");
            responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDto.setMessage("SUSPICIOUS ACTIVITIES DETECTED");

            response.sendRedirect("http://localhost:9095/github/login?");

            return responseDto;
        }

        Map<String, String> body = new HashMap<>();
        body.put("client_id", GITHUB_CLIENT_ID);
        body.put("client_secret", GITHUB_CLIENT_SECRET);
        body.put("redirect_uri", REDIRECT_URI);
        body.put("code", code);

        String form = body.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://github.com/login/oauth/access_token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        var responseBody = new JSONObject(httpResponse.body());
        String accessToken = responseBody.getString("access_token");
        log.info("Github access token: {}", accessToken);

        authSuccessResponseDto.setAccessToken(accessToken);
        authSuccessResponseDto.setRefreshToken(null);

        responseDto.setStatusCode(HttpStatus.OK.value());
        responseDto.setMessage("Successfully Authenticated User with Github");
        responseDto.setData(authSuccessResponseDto);

        return responseDto;
    }
}
