package com.br.desafioirrahbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        boolean active
) {
}
