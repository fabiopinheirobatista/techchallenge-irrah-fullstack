package com.br.desafioirrahbackend.repository;
import com.br.desafioirrahbackend.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MessageRepository extends JpaRepository<Message, UUID> {
 Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);
 List<Message> findByStatusIn(Collection<MessageStatus> statuses);
}
