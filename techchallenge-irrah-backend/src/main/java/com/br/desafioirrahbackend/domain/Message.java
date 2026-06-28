package com.br.desafioirrahbackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessagePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cost;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "processing_at")
    private Instant processingAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "read_at")
    private Instant readAt;

    public Message(Conversation conversation, String content, MessagePriority priority) {
        this.conversation = conversation;
        this.content = content.trim();
        this.direction = MessageDirection.OUTBOUND;
        this.priority = priority;
        this.status = MessageStatus.QUEUED;
        this.cost = priority.cost();
        this.createdAt = Instant.now();
    }

    public static Message inbound(Conversation conversation, String content) {
        Message message = new Message();
        message.conversation = conversation;
        message.content = content.trim();
        message.direction = MessageDirection.INBOUND;
        message.priority = MessagePriority.NORMAL;
        message.status = MessageStatus.RECEIVED;
        message.cost = BigDecimal.ZERO;
        message.createdAt = Instant.now();
        return message;
    }

    public void process() {
        if (status != MessageStatus.QUEUED) {
            throw new IllegalStateException("Only queued messages can be processed");
        }
        status = MessageStatus.PROCESSING;
        processingAt = Instant.now();
    }

    public void sent() {
        if (status != MessageStatus.PROCESSING) {
            throw new IllegalStateException("Only processing messages can be sent");
        }
        status = MessageStatus.SENT;
        sentAt = Instant.now();
    }

    public void fail(String reason) {
        status = MessageStatus.FAILED;
        failureReason = reason;
    }

    public void read() {
        if (direction != MessageDirection.INBOUND) {
            throw new IllegalStateException("Only inbound messages can be marked as read");
        }
        readAt = Instant.now();
    }
}
