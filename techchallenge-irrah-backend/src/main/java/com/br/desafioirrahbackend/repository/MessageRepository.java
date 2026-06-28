package com.br.desafioirrahbackend.repository;

import com.br.desafioirrahbackend.domain.Message;
import com.br.desafioirrahbackend.domain.MessageDirection;
import com.br.desafioirrahbackend.domain.MessageStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    Optional<Message> findByIdAndConversationId(UUID id, UUID conversationId);

    Optional<Message> findFirstByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    long countByConversationIdAndDirectionAndReadAtIsNull(UUID conversationId, MessageDirection direction);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select message from Message message
            join fetch message.conversation
            where message.status = :status
            order by message.priority desc, message.createdAt asc
            """)
    List<Message> findNextByStatus(@Param("status") MessageStatus status, Pageable pageable);
}
