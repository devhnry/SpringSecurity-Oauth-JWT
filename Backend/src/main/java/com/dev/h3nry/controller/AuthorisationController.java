package com.dev.h3nry.controller;

import com.dev.h3nry.dto.*;
import com.dev.h3nry.entity.AuthToken;
import com.dev.h3nry.repository.TokenRepository;
import com.dev.h3nry.service.AuthorisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthorisationController {

    private final AuthorisationService authorisationService;

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
     * @param loginRequest Authentication of the Oauth2 Provider
     * */
    @PostMapping("/github/login")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> githubLogin(
            @RequestBody LoginRequestDto loginRequest){
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }


    /**
     * Login Controller for OAuth2 Authentication -- GITLAB
     * @param loginRequest Authentication of the Oauth2 Provider
     * */
    @PostMapping("/gitlab/login")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> gitlabLogin(
            @RequestBody LoginRequestDto loginRequest){
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    /**
     * Login Controller for OAuth2 Authentication -- GOOGLE
     * @param loginRequest Authentication of the Oauth2 Provider
     * */
    @PostMapping("/google/login")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> googleLogin(
            @RequestBody LoginRequestDto loginRequest){
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    /**
     * Returns Success Message as well as Tokens for Oauth Authorisation
     * @param authentication Authentication of the Oauth2 Provider
     * */
    @GetMapping("/success")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> profile(Authentication authentication) {
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.authoriseSuccess(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}