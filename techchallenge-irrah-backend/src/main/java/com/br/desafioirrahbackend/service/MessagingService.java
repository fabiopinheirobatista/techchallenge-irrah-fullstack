package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.domain.Conversation;
import com.br.desafioirrahbackend.domain.FinancialTransaction;
import com.br.desafioirrahbackend.domain.Message;
import com.br.desafioirrahbackend.domain.MessageDirection;
import com.br.desafioirrahbackend.domain.TransactionType;
import com.br.desafioirrahbackend.dto.InboundMessageRequest;
import com.br.desafioirrahbackend.dto.MessageRequest;
import com.br.desafioirrahbackend.dto.MessageResponse;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.ClientRepository;
import com.br.desafioirrahbackend.repository.FinancialTransactionRepository;
import com.br.desafioirrahbackend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final ConversationService conversationService;
    private final ClientRepository clientRepository;
    private final MessageRepository messageRepository;
    private final FinancialTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public Page<MessageResponse> list(UUID clientId, UUID conversationId, Pageable pageable) {
        conversationService.findOwned(conversationId, clientId);
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(MessageResponse::from);
    }

    @Transactional
    public MessageResponse send(UUID clientId, UUID conversationId, MessageRequest request) {
        Conversation conversation = conversationService.findOwned(conversationId, clientId);
        if (!conversation.getRecipient().isActive()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_CONTENT, "Recipient is inactive");
        }

        Client client = clientRepository.findByIdForUpdate(clientId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Client not found"));
        BigDecimal cost = request.priority().cost();
        try {
            client.charge(cost);
        } catch (IllegalStateException exception) {
            throw new ApiException(HttpStatus.PAYMENT_REQUIRED, exception.getMessage());
        }

        Message message = messageRepository.save(new Message(conversation, request.content(), request.priority()));
        transactionRepository.save(new FinancialTransaction(client, TransactionType.DEBIT, cost.negate(),
                client.getBalance(), "Message charge: " + message.getId()));
        conversation.touch();
        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse receive(UUID clientId, UUID conversationId, InboundMessageRequest request) {
        Conversation conversation = conversationService.findOwned(conversationId, clientId);
        Message message = messageRepository.save(Message.inbound(conversation, request.content()));
        conversation.touch();
        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse markAsRead(UUID clientId, UUID conversationId, UUID messageId) {
        conversationService.findOwned(conversationId, clientId);
        Message message = messageRepository.findByIdAndConversationId(messageId, conversationId)
                .filter(item -> item.getDirection() == MessageDirection.INBOUND)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inbound message not found"));
        message.read();
        return MessageResponse.from(message);
    }
}
