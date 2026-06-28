package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.Role;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID token,
        Instant expiresAt,
        UUID clientId,
        String name,
        Role role
) {
}
