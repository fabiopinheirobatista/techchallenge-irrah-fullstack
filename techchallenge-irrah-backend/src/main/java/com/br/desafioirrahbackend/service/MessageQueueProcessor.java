package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Message;
import com.br.desafioirrahbackend.domain.MessageStatus;
import com.br.desafioirrahbackend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.messaging.processing-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class MessageQueueProcessor {

    private final MessageRepository messageRepository;

    @Scheduled(fixedDelayString = "${app.messaging.processing-delay:1000}")
    @Transactional
    public void processNext() {
        nextQueued().ifPresent(message -> {
            message.process();
            message.sent();
        });
    }

    private Optional<Message> nextQueued() {
        return messageRepository.findNextByStatus(MessageStatus.QUEUED, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }
}
