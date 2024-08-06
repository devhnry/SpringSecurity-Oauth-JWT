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

    @GetMapping("/dashboard")
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
     * Login Up Controller for JWT Authentication
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
     * Returns Success Message as well as Tokens for Oauth Authorisation
     * @param authentication LoginRequestDto that takes email, password.
     * */
    @GetMapping("/success")
    public ResponseEntity<DefaultResponseDto<AuthSuccessResponseDto>> profile(Authentication authentication) {
        DefaultResponseDto<AuthSuccessResponseDto> response = authorisationService.authoriseSuccess(authentication);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}