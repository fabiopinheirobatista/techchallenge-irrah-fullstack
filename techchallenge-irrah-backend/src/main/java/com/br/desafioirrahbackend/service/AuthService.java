package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.AuthToken;
import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.domain.DocumentType;
import com.br.desafioirrahbackend.config.AuthProperties;
import com.br.desafioirrahbackend.dto.AuthRequest;
import com.br.desafioirrahbackend.dto.AuthResponse;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.AuthTokenRepository;
import com.br.desafioirrahbackend.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final AuthTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthProperties authProperties;

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        String digits = request.documentId().replaceAll("\\D", "");
        DocumentType type = digits.length() == 11 ? DocumentType.CPF : DocumentType.CNPJ;
        try {
            digits = DocumentValidator.normalizeAndValidate(digits, type);
        } catch (IllegalArgumentException exception) {
            throw invalidCredentials();
        }
        Client client = clientRepository.findByDocumentId(digits).orElseThrow(this::invalidCredentials);
        if (!client.isActive() || !passwordEncoder.matches(request.password(), client.getPasswordHash())) {
            throw invalidCredentials();
        }
        Instant expiresAt = Instant.now().plus(authProperties.tokenTtl());
        AuthToken token = tokenRepository.save(new AuthToken(client, expiresAt));
        return new AuthResponse(token.getId(), expiresAt, client.getId(), client.getName(), client.getRole());
    }

    @Transactional
    public void logout(String rawToken) {
        try {
            tokenRepository.findById(UUID.fromString(rawToken)).ifPresent(token -> token.revoke());
        } catch (IllegalArgumentException ignored) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid authentication token");
        }
    }

    private ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
