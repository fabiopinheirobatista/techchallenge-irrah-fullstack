package com.br.desafioirrahbackend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConversationRequest(@NotNull UUID recipientId) {
}
