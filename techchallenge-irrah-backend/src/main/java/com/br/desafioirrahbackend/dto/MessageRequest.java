package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.MessagePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank @Size(max = 2000) String content,
        @NotNull MessagePriority priority
) {
}
