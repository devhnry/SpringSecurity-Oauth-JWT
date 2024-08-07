package com.dev.h3nry.repository;

import com.dev.h3nry.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<AuthToken, Long> {
    @Query("""
            select t from AuthToken t inner join AppUser u on t.user.username = u.username 
            where u.username = :username
        """
    )
    Optional<AuthToken> findTokenByUser(String username);
}
