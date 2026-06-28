package com.br.desafioirrahbackend.controller;

import com.br.desafioirrahbackend.dto.ClientResponse;
import com.br.desafioirrahbackend.dto.FinancialTransactionResponse;
import com.br.desafioirrahbackend.dto.PasswordChangeRequest;
import com.br.desafioirrahbackend.security.AuthenticatedClient;
import com.br.desafioirrahbackend.service.BillingService;
import com.br.desafioirrahbackend.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients/me")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final BillingService billingService;

    @GetMapping
    public ClientResponse me(@AuthenticationPrincipal AuthenticatedClient client) {
        return clientService.find(client.id());
    }

    @GetMapping("/transactions")
    public Page<FinancialTransactionResponse> transactions(
            @AuthenticationPrincipal AuthenticatedClient client,
            Pageable pageable
    ) {
        return billingService.list(client.id(), pageable);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal AuthenticatedClient client,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        clientService.changeOwnPassword(client.id(), request);
    }
}
