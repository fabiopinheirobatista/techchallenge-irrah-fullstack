package com.br.desafioirrahbackend.controller;

import com.br.desafioirrahbackend.dto.ConversationRequest;
import com.br.desafioirrahbackend.dto.ConversationResponse;
import com.br.desafioirrahbackend.dto.InboundMessageRequest;
import com.br.desafioirrahbackend.dto.MessageRequest;
import com.br.desafioirrahbackend.dto.MessageResponse;
import com.br.desafioirrahbackend.security.AuthenticatedClient;
import com.br.desafioirrahbackend.service.ConversationService;
import com.br.desafioirrahbackend.service.MessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@PreAuthorize("hasRole('CLIENT')")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final MessagingService messagingService;

    @GetMapping
    public Page<ConversationResponse> list(@AuthenticationPrincipal AuthenticatedClient client, Pageable pageable) {
        return conversationService.list(client.id(), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse create(@AuthenticationPrincipal AuthenticatedClient client,
                                       @Valid @RequestBody ConversationRequest request) {
        return conversationService.create(client.id(), request);
    }

    @GetMapping("/{conversationId}/messages")
    public Page<MessageResponse> messages(@AuthenticationPrincipal AuthenticatedClient client,
                                          @PathVariable UUID conversationId, Pageable pageable) {
        return messagingService.list(client.id(), conversationId, pageable);
    }

    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@AuthenticationPrincipal AuthenticatedClient client,
                                @PathVariable UUID conversationId,
                                @Valid @RequestBody MessageRequest request) {
        return messagingService.send(client.id(), conversationId, request);
    }

    @PostMapping("/{conversationId}/messages/inbound")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse receive(@AuthenticationPrincipal AuthenticatedClient client,
                                   @PathVariable UUID conversationId,
                                   @Valid @RequestBody InboundMessageRequest request) {
        return messagingService.receive(client.id(), conversationId, request);
    }

    @PatchMapping("/{conversationId}/messages/{messageId}/read")
    public MessageResponse markAsRead(@AuthenticationPrincipal AuthenticatedClient client,
                                      @PathVariable UUID conversationId, @PathVariable UUID messageId) {
        return messagingService.markAsRead(client.id(), conversationId, messageId);
    }
}
