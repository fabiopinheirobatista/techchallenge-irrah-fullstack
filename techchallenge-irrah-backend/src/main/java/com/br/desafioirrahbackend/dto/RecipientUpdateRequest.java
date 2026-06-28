package com.br.desafioirrahbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipientUpdateRequest(@NotBlank @Size(max = 150) String name, boolean active) {
}
