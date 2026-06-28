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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "document_id", nullable = false, unique = true, length = 14)
    private String documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 4)
    private DocumentType documentType;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", length = 8)
    private PlanType planType;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance;

    @Column(name = "monthly_limit", precision = 12, scale = 2)
    private BigDecimal monthlyLimit;

    @Column(name = "monthly_usage", precision = 12, scale = 2)
    private BigDecimal monthlyUsage;

    @Column(name = "billing_cycle_month")
    private LocalDate billingCycleMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Client(String documentId, DocumentType documentType, String name, String passwordHash, PlanType planType) {
        this.documentId = documentId;
        this.documentType = documentType;
        this.name = name;
        this.passwordHash = passwordHash;
        this.planType = planType;
        this.role = Role.CLIENT;
        this.active = true;
        this.updatedAt = Instant.now();
        if (planType == PlanType.PREPAID) {
            this.balance = BigDecimal.ZERO;
        } else {
            this.monthlyLimit = BigDecimal.ZERO;
            this.monthlyUsage = BigDecimal.ZERO;
            this.billingCycleMonth = LocalDate.now().withDayOfMonth(1);
        }
    }

    public void update(String name, boolean active) {
        this.name = name;
        this.active = active;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = Instant.now();
    }
}
