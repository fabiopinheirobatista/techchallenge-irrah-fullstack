package com.br.desafioirrahbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "conversations") @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "client_id") private Client client;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "recipient_id") private Recipient recipient;
    @Column(name = "created_at", insertable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    public Conversation(Client client, Recipient recipient) { this.client = client; this.recipient = recipient; this.updatedAt = Instant.now(); }
    public void touch() { this.updatedAt = Instant.now(); }
}
