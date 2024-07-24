package com.dev.h3nry.service;

import com.dev.h3nry.entity.AuthToken;
import org.springframework.security.core.Authentication;

public interface TokenService {
    AuthToken handleInitialAuthenticationSuccess(Authentication authentication, String clientRegistrationId);
}
