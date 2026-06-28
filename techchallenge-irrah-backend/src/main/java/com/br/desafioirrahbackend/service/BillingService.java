package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.domain.FinancialTransaction;
import com.br.desafioirrahbackend.domain.PlanType;
import com.br.desafioirrahbackend.domain.TransactionType;
import com.br.desafioirrahbackend.dto.FinancialTransactionResponse;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.ClientRepository;
import com.br.desafioirrahbackend.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final ClientRepository clientRepository;
    private final FinancialTransactionRepository transactionRepository;

    @Transactional
    public FinancialTransactionResponse addCredit(UUID clientId, BigDecimal amount) {
        Client client = lockedClient(clientId, PlanType.PREPAID);
        client.addCredit(amount);
        return FinancialTransactionResponse.from(transactionRepository.save(new FinancialTransaction(client,
                TransactionType.CREDIT, amount, client.getBalance(), "Credit added by administrator")));
    }

    @Transactional
    public FinancialTransactionResponse adjustMonthlyLimit(UUID clientId, BigDecimal limit) {
        Client client = lockedClient(clientId, PlanType.POSTPAID);
        client.adjustMonthlyLimit(limit);
        return FinancialTransactionResponse.from(transactionRepository.save(new FinancialTransaction(client,
                TransactionType.LIMIT_ADJUSTMENT, limit, null, "Monthly limit adjusted by administrator")));
    }

    @Transactional(readOnly = true)
    public Page<FinancialTransactionResponse> list(UUID clientId, Pageable pageable) {
        if (!clientRepository.existsById(clientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Client not found");
        }
        return transactionRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
                .map(FinancialTransactionResponse::from);
    }

    private Client lockedClient(UUID clientId, PlanType expectedPlan) {
        Client client = clientRepository.findByIdForUpdate(clientId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Client not found"));
        if (client.getPlanType() != expectedPlan) {
            throw new ApiException(HttpStatus.CONFLICT, "Operation is incompatible with the client plan");
        }
        return client;
    }
}
