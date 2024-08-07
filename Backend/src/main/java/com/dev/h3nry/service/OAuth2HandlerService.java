package com.dev.h3nry.service;

import com.dev.h3nry.dto.AuthSuccessResponseDto;
import com.dev.h3nry.dto.DefaultResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.net.URISyntaxException;

public interface OAuth2HandlerService {
    void githubLogin(HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException;
    DefaultResponseDto<AuthSuccessResponseDto> handleGithubRedirect(String code, String state, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException;
}
