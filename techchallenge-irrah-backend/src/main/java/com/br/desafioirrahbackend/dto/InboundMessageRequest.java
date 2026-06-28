package com.br.desafioirrahbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InboundMessageRequest(@NotBlank @Size(max = 2000) String content) {
}
