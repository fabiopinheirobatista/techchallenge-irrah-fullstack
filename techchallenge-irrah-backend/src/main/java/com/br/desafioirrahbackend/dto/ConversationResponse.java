package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.Conversation;
import com.br.desafioirrahbackend.domain.ContactType;
import com.br.desafioirrahbackend.domain.Message;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id, UUID recipientId, String recipientName, String recipientContact,
        ContactType contactType, String lastMessageContent, Instant lastMessageTime,
        long unreadCount, Instant createdAt, Instant updatedAt
) {
    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(conversation.getId(), conversation.getRecipient().getId(),
                conversation.getRecipient().getName(), conversation.getRecipient().getContact(),
                conversation.getRecipient().getContactType(), null, null, 0,
                conversation.getCreatedAt(), conversation.getUpdatedAt());
    }

    public static ConversationResponse from(Conversation conversation, Message lastMessage, long unreadCount) {
        return new ConversationResponse(conversation.getId(), conversation.getRecipient().getId(),
                conversation.getRecipient().getName(), conversation.getRecipient().getContact(),
                conversation.getRecipient().getContactType(),
                lastMessage == null ? null : lastMessage.getContent(),
                lastMessage == null ? null : lastMessage.getCreatedAt(), unreadCount,
                conversation.getCreatedAt(), conversation.getUpdatedAt());
    }
}
