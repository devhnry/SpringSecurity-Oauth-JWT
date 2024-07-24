package com.dev.h3nry.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
@Table(name = "token")
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    private Instant expiresAt;
    private Instant issuedAt;

    // Mapping the Token to a particular user.
    @OneToOne
    @JoinColumn(name = "username")
    private AppUser user;

}
