package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecipientRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 150) String contact,
        @NotNull ContactType contactType
) {
}
