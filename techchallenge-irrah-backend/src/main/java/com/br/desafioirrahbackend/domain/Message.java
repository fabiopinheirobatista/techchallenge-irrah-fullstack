package com.br.desafioirrahbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "messages") @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "conversation_id") private Conversation conversation;
    @Column(nullable = false, length = 2000) private String content;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MessageDirection direction;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MessagePriority priority;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MessageStatus status;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal cost;
    @Column(name = "failure_reason") private String failureReason;
    @Column(name = "created_at", insertable = false, updatable = false) private Instant createdAt;
    @Column(name = "processing_at") private Instant processingAt;
    @Column(name = "sent_at") private Instant sentAt;
    @Column(name = "read_at") private Instant readAt;
    public Message(Conversation c, String content, MessagePriority priority, BigDecimal cost) {
        this.conversation=c; this.content=content; this.direction=MessageDirection.OUTBOUND;
        this.priority=priority; this.status=MessageStatus.QUEUED; this.cost=cost;
    }
    public static Message inbound(Conversation c, String content) {
        Message m=new Message(c,content,MessagePriority.NORMAL,BigDecimal.ZERO); m.direction=MessageDirection.INBOUND; m.status=MessageStatus.RECEIVED; return m;
    }
    public void queue(){status=MessageStatus.QUEUED; processingAt=null;}
    public void process(){status=MessageStatus.PROCESSING; processingAt=Instant.now();}
    public void sent(){status=MessageStatus.SENT; sentAt=Instant.now();}
    public void fail(String reason){status=MessageStatus.FAILED; failureReason=reason;}
    public void read(){if(direction==MessageDirection.INBOUND) readAt=Instant.now();}
}
