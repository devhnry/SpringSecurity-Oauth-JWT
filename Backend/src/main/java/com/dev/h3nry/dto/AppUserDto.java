package com.dev.h3nry.dto;

import com.dev.h3nry.enums.SignUpMethod;

import java.time.LocalDateTime;

public record AppUserDto(
    Long userId,
    String name,
    String username,
    String email,
    String gitlabId,
    String githubId,
    SignUpMethod signUpMethod,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){}
