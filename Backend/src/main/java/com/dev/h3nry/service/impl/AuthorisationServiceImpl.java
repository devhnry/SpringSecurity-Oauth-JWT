package com.dev.h3nry.service.impl;

import com.dev.h3nry.dto.*;
import com.dev.h3nry.entity.AppUser;
import com.dev.h3nry.entity.AuthToken;
import com.dev.h3nry.enums.SignUpMethod;
import com.dev.h3nry.repository.TokenRepository;
import com.dev.h3nry.repository.UserRepository;
import com.dev.h3nry.service.AuthorisationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service @Slf4j
@RequiredArgsConstructor
public class AuthorisationServiceImpl implements AuthorisationService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    public DefaultResponseDto<AuthSuccessResponseDto> authoriseSuccess(Authentication authentication) {
        DefaultResponseDto<AuthSuccessResponseDto> response = new DefaultResponseDto<>();

        AuthSuccessResponseDto authSuccessResponseDto = new AuthSuccessResponseDto();
        Optional<AuthToken> authToken = tokenRepository.findTokenByUser(authentication.getName());

        // Checks for the token...
        if(authToken.isPresent()){
            AuthToken auth = authToken.get();
            authSuccessResponseDto = new AuthSuccessResponseDto();
            authSuccessResponseDto.setAccessToken(auth.getAccessToken());
            authSuccessResponseDto.setRefreshToken(auth.getRefreshToken());
        }

        // Returns the AccessToken and Refresh Token...
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Successfully Authenticated");
        response.setData(authSuccessResponseDto);

        return response;
    }

    @Override
    public DefaultResponseDto<AuthSuccessResponseDto> login(LoginRequestDto loginRequestDto) {
        return null;
    }

    @Override
    public DefaultResponseDto<AppUserDto> signup(SignUpRequestDto signUpRequestDto) {
        DefaultResponseDto<AppUserDto> response = new DefaultResponseDto<>();

        AppUser user = new AppUser().builder()
                .name(signUpRequestDto.getFullName())
                .email(signUpRequestDto.getEmail())
                .password(signUpRequestDto.getPassword())
                .username(signUpRequestDto.getUsername())
                .signUpMethod(SignUpMethod.JWT)
                .build();
        
        AppUser savedUser = userRepository.save(user);

        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Successfully Signed Up");
        response.setData(toDto(savedUser));

        return null;
    }

    public static AppUserDto toDto(AppUser appUser) {
        if (appUser == null) return null;
        return new AppUserDto(
                appUser.getUserId(),
                appUser.getName(),
                appUser.getUsername(),
                appUser.getEmail(),
                appUser.getGitLabId(),
                appUser.getGitHubId(),
                appUser.getSignUpMethod(),
                appUser.getCreatedAt(),
                appUser.getUpdatedAt()
        );
    }
}
