package com.br.desafioirrahbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        String currentPassword,
        @NotBlank @Size(min = 8, max = 72) String newPassword
) {
}
