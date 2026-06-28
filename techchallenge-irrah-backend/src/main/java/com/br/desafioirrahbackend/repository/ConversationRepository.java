package com.br.desafioirrahbackend.repository;

import com.br.desafioirrahbackend.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @EntityGraph(attributePaths = "recipient")
    Optional<Conversation> findByClientIdAndRecipientId(UUID clientId, UUID recipientId);

    @EntityGraph(attributePaths = "recipient")
    Page<Conversation> findByClientIdOrderByUpdatedAtDesc(UUID clientId, Pageable pageable);

    @EntityGraph(attributePaths = {"client", "recipient"})
    Optional<Conversation> findByIdAndClientId(UUID id, UUID clientId);
}
