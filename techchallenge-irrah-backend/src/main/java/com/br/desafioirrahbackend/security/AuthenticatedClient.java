package com.br.desafioirrahbackend.security;

import com.br.desafioirrahbackend.domain.Role;

import java.util.UUID;

public record AuthenticatedClient(UUID id, String name, Role role) {
}
