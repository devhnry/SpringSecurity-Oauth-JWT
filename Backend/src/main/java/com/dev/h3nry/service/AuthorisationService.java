package com.dev.h3nry.service;

import com.dev.h3nry.dto.AuthSuccessResponseDto;
import com.dev.h3nry.dto.DefaultResponseDto;
import com.dev.h3nry.dto.LoginRequestDto;
import com.dev.h3nry.dto.SignUpRequestDto;
import org.springframework.security.core.Authentication;

public interface AuthorisationService {
    DefaultResponseDto<AuthSuccessResponseDto> authoriseSuccess(Authentication authentication);
    DefaultResponseDto<AuthSuccessResponseDto> login(LoginRequestDto loginRequestDto);
    DefaultResponseDto<AuthSuccessResponseDto> signup(SignUpRequestDto signUpRequestDto);
}
