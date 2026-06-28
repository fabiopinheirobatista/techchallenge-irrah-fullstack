package com.br.desafioirrahbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name="financial_transactions") @NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FinancialTransaction {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="client_id") private Client client;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TransactionType type;
    @Column(nullable=false) private BigDecimal amount;
    @Column(name="balance_after") private BigDecimal balanceAfter;
    private String description;
    public FinancialTransaction(Client c, TransactionType t, BigDecimal amount, BigDecimal after, String description){this.client=c;this.type=t;this.amount=amount;this.balanceAfter=after;this.description=description;}
}
