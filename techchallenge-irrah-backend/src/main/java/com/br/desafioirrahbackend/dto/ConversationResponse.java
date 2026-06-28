package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.Conversation;
import com.br.desafioirrahbackend.domain.ContactType;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id, UUID recipientId, String recipientName, String recipientContact,
        ContactType contactType, Instant createdAt, Instant updatedAt
) {
    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(conversation.getId(), conversation.getRecipient().getId(),
                conversation.getRecipient().getName(), conversation.getRecipient().getContact(),
                conversation.getRecipient().getContactType(), conversation.getCreatedAt(), conversation.getUpdatedAt());
    }
}
