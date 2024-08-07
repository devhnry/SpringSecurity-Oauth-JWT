package com.dev.h3nry.service.impl;

import com.dev.h3nry.service.OAuth2HandlerService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service @Slf4j
@RequiredArgsConstructor
public class OAuth2HandlerServiceImpl implements OAuth2HandlerService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String GITHUB_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String REDIRECT_URI;

    private final String STATE = "HDJGHGFHJKMSOHNUBSHBCNZJNISYVBSCPAEBYAVBAPUBEVRYVABCANIURBAYBSNBAYRBVPNCEAUVBR";

    @Override
    public void githubLogin(HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {
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
    public void handleGithubRedirect(String code, String state) throws URISyntaxException, IOException, InterruptedException {

    }
}
