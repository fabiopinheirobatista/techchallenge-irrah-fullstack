package com.br.desafioirrahbackend.controller;

import com.br.desafioirrahbackend.dto.RecipientRequest;
import com.br.desafioirrahbackend.dto.RecipientResponse;
import com.br.desafioirrahbackend.dto.RecipientUpdateRequest;
import com.br.desafioirrahbackend.service.RecipientService;
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
@RequestMapping("/api/recipients")
@RequiredArgsConstructor
public class RecipientController {

    private final RecipientService recipientService;

    @GetMapping
    public Page<RecipientResponse> list(Pageable pageable) {
        return recipientService.list(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipientResponse create(@Valid @RequestBody RecipientRequest request) {
        return recipientService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RecipientResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody RecipientUpdateRequest request
    ) {
        return recipientService.update(id, request.name(), request.active());
    }
}
