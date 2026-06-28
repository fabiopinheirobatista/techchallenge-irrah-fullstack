package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.Message;
import com.br.desafioirrahbackend.domain.MessageDirection;
import com.br.desafioirrahbackend.domain.MessagePriority;
import com.br.desafioirrahbackend.domain.MessageStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id, UUID conversationId, String content, MessageDirection direction,
        MessagePriority priority, MessageStatus status, BigDecimal cost,
        String failureReason, Instant createdAt, Instant sentAt, Instant readAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getId(), message.getConversation().getId(), message.getContent(),
                message.getDirection(), message.getPriority(), message.getStatus(), message.getCost(),
                message.getFailureReason(), message.getCreatedAt(), message.getSentAt(), message.getReadAt());
    }
}
