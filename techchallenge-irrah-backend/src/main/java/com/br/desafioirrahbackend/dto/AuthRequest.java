package com.br.desafioirrahbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank String documentId,
        @NotBlank String password
) {
}
