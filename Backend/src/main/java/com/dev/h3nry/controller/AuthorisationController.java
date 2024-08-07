package com.dev.h3nry.controller;

import com.dev.h3nry.dto.*;
import com.dev.h3nry.entity.AuthToken;
import com.dev.h3nry.repository.TokenRepository;
import com.dev.h3nry.service.AuthorisationService;
import com.dev.h3nry.service.OAuth2HandlerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthorisationController {

    private final AuthorisationService authorisationService;
    private final OAuth2HandlerService oAuth2HandlerService;

    @GetMapping("/api/v1/dashboard")
    public ResponseEntity<String> getAuthorisation() {
        return ResponseEntity.ok("Welcome to this Demo APPLICATION");
    }

    /**
     * Sign Up Controller for JWT Authentication
     * @param signupRequest SignUpRequestDto that takes email, password, and username
     * */
    @PostMapping("/auth/signup")
    public ResponseEntity<DefaultResponseDto<AppUserDto>> signup(
            @RequestBody SignUpRequestDto signupRequest){
        DefaultResponseDto<AppUserDto> response = authorisationService.signup(signupRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Login Controller for JWT Authentication
     * @param loginRequest LoginRequestDto that takes email, password.
     * */
    @PostMapping("/auth/login")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> login(
            @RequestBody LoginRequestDto loginRequest){
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    /**
     * Login Controller for OAuth2 Authentication -- GITHUB
     * */
    @GetMapping("/github/login")
    public ResponseEntity<String> githubLogin(HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {
        oAuth2HandlerService.githubLogin(response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Done");

    }

    /**
     * Controller to handle OAuth2 Authentication Redirect -- GITHUB
     */
    @GetMapping("/login/oauth2/code/github")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> handleGitHubRedirect(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws URISyntaxException, IOException, InterruptedException {
        DefaultResponseDto<AuthSuccessResponseDto> githubResponse = oAuth2HandlerService.handleGithubRedirect(code, state, response);
        return ResponseEntity.status(200).body(githubResponse);
    }

    /**
     * Controller for GITLAB OAuth2 Authentication
     */
    @PostMapping("/gitlab/login")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> gitlabLogin(
            @RequestBody LoginRequestDto loginRequest){
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    /**
     * Controller for GOOGLE OAuth2 Authentication
     */
    @PostMapping("/google/login")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> googleLogin(
            @RequestBody LoginRequestDto loginRequest){
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}