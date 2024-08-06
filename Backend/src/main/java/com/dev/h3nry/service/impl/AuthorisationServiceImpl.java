package com.dev.h3nry.service.impl;

import com.dev.h3nry.config.SecurityPasswordEncoder;
import com.dev.h3nry.dto.*;
import com.dev.h3nry.entity.AppUser;
import com.dev.h3nry.entity.AuthToken;
import com.dev.h3nry.enums.SignUpMethod;
import com.dev.h3nry.repository.TokenRepository;
import com.dev.h3nry.repository.UserRepository;
import com.dev.h3nry.service.AuthorisationService;
import com.dev.h3nry.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;

@Service @Slf4j
@RequiredArgsConstructor
public class AuthorisationServiceImpl implements AuthorisationService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SecurityPasswordEncoder securityPasswordEncoder;
    private final AuthenticationManager authenticationManager;

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
        DefaultResponseDto<AuthSuccessResponseDto> response = new DefaultResponseDto<>();
        AuthSuccessResponseDto authSuccessResponseDto = new AuthSuccessResponseDto();
        AuthToken authToken = new AuthToken();

        try {
            AppUser appUser;
            Optional<AppUser> existingUser = userRepository.findAppUserByEmail(loginRequestDto.getEmail());
            if(!existingUser.isPresent()){
               response.setStatusCode(HttpStatus.BAD_REQUEST.value());
               response.setMessage("Invalid email: could not find user");
               return response;
            }

            Optional<AuthToken> existingToken = tokenRepository.findTokenByUser(existingUser.get().getUsername());
            if(existingToken.isPresent()){
                authToken = existingToken.get();
            }

            appUser = existingUser.get();
            if(securityPasswordEncoder.passwordEncoder().matches(loginRequestDto.getPassword(), appUser.getPassword())){
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));


                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Successfully Logged in");

                String accessToken = jwtService.createToken(appUser);
                String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), appUser);

                authToken.setUser(appUser);
                authToken.setIssuedAt(Instant.now());
                authToken.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));

                authToken.setAccessToken(accessToken);
                authToken.setRefreshToken(refreshToken);

                authSuccessResponseDto.setAccessToken(accessToken);
                authSuccessResponseDto.setRefreshToken(refreshToken);

                tokenRepository.save(authToken);
                response.setData(authSuccessResponseDto);
            }else{
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Invalid Password: check credentials");
                return response;
            }
        } catch (AuthenticationException e) {
            log.error("Authentication exception", e.getMessage());
        }

        return response;
    }

    @Override
    public DefaultResponseDto<AppUserDto> signup(SignUpRequestDto signUpRequestDto) {
        DefaultResponseDto<AppUserDto> response = new DefaultResponseDto<>();

        AppUser user = new AppUser().builder()
                .name(signUpRequestDto.getFullName())
                .email(signUpRequestDto.getEmail())
                .password(securityPasswordEncoder.passwordEncoder().encode(signUpRequestDto.getPassword()))
                .username(signUpRequestDto.getUsername())
                .signUpMethod(SignUpMethod.JWT)
                .build();

        AppUser savedUser = userRepository.save(user);

        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Successfully Signed Up");
        response.setData(toDto(savedUser));

        return response;
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
