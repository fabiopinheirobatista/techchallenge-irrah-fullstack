package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.FinancialTransaction;
import com.br.desafioirrahbackend.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FinancialTransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        Instant createdAt
) {
    public static FinancialTransactionResponse from(FinancialTransaction transaction) {
        return new FinancialTransactionResponse(transaction.getId(), transaction.getType(), transaction.getAmount(),
                transaction.getBalanceAfter(), transaction.getDescription(), transaction.getCreatedAt());
    }
}
