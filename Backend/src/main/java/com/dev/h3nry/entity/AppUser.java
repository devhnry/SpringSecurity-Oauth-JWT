package com.dev.h3nry.entity;

import com.dev.h3nry.enums.SignUpMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity @Builder
@AllArgsConstructor @NoArgsConstructor
@Setter @Getter @ToString
public class AppUser implements UserDetails {

    @Id
    @Column(unique=true, name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", unique = true)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Builder.Default
    private Boolean isActive = true;

    /* This will allow us to easily use the API for GitLab*/
    @Column(name = "gitlab_id", unique = true)
    private String gitLabId;

    /* This will allow us to easily use the API for GitHub */
    @Column(name = "github_id", unique = true)
    private String gitHubId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private SignUpMethod signUpMethod;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
