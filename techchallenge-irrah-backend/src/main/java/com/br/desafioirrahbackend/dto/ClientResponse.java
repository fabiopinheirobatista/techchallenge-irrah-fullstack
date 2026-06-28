package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.domain.DocumentType;
import com.br.desafioirrahbackend.domain.PlanType;
import com.br.desafioirrahbackend.domain.Role;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String documentId,
        DocumentType documentType,
        String name,
        PlanType planType,
        BigDecimal balance,
        BigDecimal monthlyLimit,
        BigDecimal monthlyUsage,
        Role role,
        boolean active
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getId(), client.getDocumentId(), client.getDocumentType(),
                client.getName(), client.getPlanType(), client.getBalance(), client.getMonthlyLimit(),
                client.getMonthlyUsage(), client.getRole(), client.isActive());
    }
}
