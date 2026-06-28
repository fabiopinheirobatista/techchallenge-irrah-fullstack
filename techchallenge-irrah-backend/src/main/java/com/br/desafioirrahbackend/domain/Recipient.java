package com.br.desafioirrahbackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recipients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipient {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 10)
    private ContactType contactType;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Recipient(String name, String contact, ContactType contactType) {
        this.name = name;
        this.contact = contact;
        this.contactType = contactType;
        this.updatedAt = Instant.now();
    }

    public void update(String name, boolean active) {
        this.name = name;
        this.active = active;
        this.updatedAt = Instant.now();
    }
}
