package com.br.desafioirrahbackend.controller;

import com.br.desafioirrahbackend.dto.ClientRequest;
import com.br.desafioirrahbackend.dto.ClientResponse;
import com.br.desafioirrahbackend.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse register(@Valid @RequestBody ClientRequest request) {
        return clientService.create(request);
    }
}
