package com.br.desafioirrahbackend.controller;

import com.br.desafioirrahbackend.dto.AmountRequest;
import com.br.desafioirrahbackend.dto.ClientRequest;
import com.br.desafioirrahbackend.dto.ClientResponse;
import com.br.desafioirrahbackend.dto.ClientUpdateRequest;
import com.br.desafioirrahbackend.dto.FinancialTransactionResponse;
import com.br.desafioirrahbackend.dto.PasswordChangeRequest;
import com.br.desafioirrahbackend.service.BillingService;
import com.br.desafioirrahbackend.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/clients")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminClientController {

    private final ClientService clientService;
    private final BillingService billingService;

    @GetMapping
    public Page<ClientResponse> list(Pageable pageable) {
        return clientService.list(pageable);
    }

    @GetMapping("/{id}")
    public ClientResponse find(@PathVariable UUID id) {
        return clientService.find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(@Valid @RequestBody ClientRequest request) {
        return clientService.create(request);
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable UUID id, @Valid @RequestBody ClientUpdateRequest request) {
        return clientService.update(id, request);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@PathVariable UUID id, @Valid @RequestBody PasswordChangeRequest request) {
        clientService.resetPassword(id, request);
    }

    @PostMapping("/{id}/credits")
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialTransactionResponse addCredit(@PathVariable UUID id,
                                                   @Valid @RequestBody AmountRequest request) {
        return billingService.addCredit(id, request.amount());
    }

    @PutMapping("/{id}/monthly-limit")
    public FinancialTransactionResponse adjustMonthlyLimit(@PathVariable UUID id,
                                                            @Valid @RequestBody AmountRequest request) {
        return billingService.adjustMonthlyLimit(id, request.amount());
    }
}
