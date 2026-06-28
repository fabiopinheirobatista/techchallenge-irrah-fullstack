package com.br.desafioirrahbackend.repository;

import com.br.desafioirrahbackend.domain.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {

    @EntityGraph(attributePaths = "client")
    Optional<AuthToken> findByIdAndRevokedFalseAndExpiresAtAfter(UUID id, Instant now);
}
