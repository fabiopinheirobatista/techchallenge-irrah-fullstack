package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.domain.Conversation;
import com.br.desafioirrahbackend.domain.Message;
import com.br.desafioirrahbackend.domain.MessageDirection;
import com.br.desafioirrahbackend.domain.Recipient;
import com.br.desafioirrahbackend.dto.ConversationRequest;
import com.br.desafioirrahbackend.dto.ConversationResponse;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.ClientRepository;
import com.br.desafioirrahbackend.repository.ConversationRepository;
import com.br.desafioirrahbackend.repository.RecipientRepository;
import com.br.desafioirrahbackend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ClientRepository clientRepository;
    private final RecipientRepository recipientRepository;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public Page<ConversationResponse> list(UUID clientId, Pageable pageable) {
        return conversationRepository.findByClientIdOrderByUpdatedAtDesc(clientId, pageable).map(this::withSummary);
    }

    @Transactional
    public ConversationResponse create(UUID clientId, ConversationRequest request) {
        return conversationRepository.findByClientIdAndRecipientId(clientId, request.recipientId())
                .map(ConversationResponse::from)
                .orElseGet(() -> createConversation(clientId, request.recipientId()));
    }

    Conversation findOwned(UUID conversationId, UUID clientId) {
        return conversationRepository.findByIdAndClientId(conversationId, clientId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Conversation not found"));
    }

    private ConversationResponse createConversation(UUID clientId, UUID recipientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Client not found"));
        Recipient recipient = recipientRepository.findById(recipientId)
                .filter(Recipient::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Active recipient not found"));
        return ConversationResponse.from(conversationRepository.save(new Conversation(client, recipient)));
    }

    private ConversationResponse withSummary(Conversation conversation) {
        Message lastMessage = messageRepository
                .findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId())
                .orElse(null);
        long unreadCount = messageRepository.countByConversationIdAndDirectionAndReadAtIsNull(
                conversation.getId(), MessageDirection.INBOUND);
        return ConversationResponse.from(conversation, lastMessage, unreadCount);
    }
}
