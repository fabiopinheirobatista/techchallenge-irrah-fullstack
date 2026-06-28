package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.DocumentType;
import com.br.desafioirrahbackend.domain.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank String documentId,
        @NotNull DocumentType documentType,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull PlanType planType
) {
}
