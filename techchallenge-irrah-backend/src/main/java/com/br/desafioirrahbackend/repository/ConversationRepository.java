package com.br.desafioirrahbackend.repository;
import com.br.desafioirrahbackend.domain.Conversation;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
 @EntityGraph(attributePaths = "recipient")
 Optional<Conversation> findByClientIdAndRecipientId(UUID clientId, UUID recipientId);
 @EntityGraph(attributePaths = "recipient")
 Page<Conversation> findByClientId(UUID clientId, Pageable pageable);
 @EntityGraph(attributePaths = {"client", "recipient"})
 Optional<Conversation> findByIdAndClientId(UUID id, UUID clientId);
}
