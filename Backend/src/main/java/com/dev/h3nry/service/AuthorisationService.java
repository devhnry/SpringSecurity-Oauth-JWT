package com.dev.h3nry.service;

import com.dev.h3nry.dto.*;
import org.springframework.security.core.Authentication;

public interface AuthorisationService {
    DefaultResponseDto<AuthSuccessResponseDto> authoriseSuccess(Authentication authentication);
    DefaultResponseDto<AuthSuccessResponseDto> login(LoginRequestDto loginRequestDto);
    DefaultResponseDto<AppUserDto> signup(SignUpRequestDto signUpRequestDto);
}
