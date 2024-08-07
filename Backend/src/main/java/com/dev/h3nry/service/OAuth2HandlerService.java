package com.dev.h3nry.service;

import com.dev.h3nry.dto.DefaultResponseDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface OAuth2HandlerService {
    void githubLogin(HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException;
    void handleGithubRedirect(String code, String state) throws URISyntaxException, IOException, InterruptedException;
}
