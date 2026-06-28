package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.dto.ClientRequest;
import com.br.desafioirrahbackend.dto.ClientResponse;
import com.br.desafioirrahbackend.dto.ClientUpdateRequest;
import com.br.desafioirrahbackend.dto.PasswordChangeRequest;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClientResponse create(ClientRequest request) {
        String documentId;
        try {
            documentId = DocumentValidator.normalizeAndValidate(request.documentId(), request.documentType());
        } catch (IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        if (clientRepository.existsByDocumentId(documentId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Document is already registered");
        }
        Client client = new Client(documentId, request.documentType(), request.name().trim(),
                passwordEncoder.encode(request.password()), request.planType());
        return ClientResponse.from(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> list(Pageable pageable) {
        return clientRepository.findAll(pageable).map(ClientResponse::from);
    }

    @Transactional(readOnly = true)
    public ClientResponse find(UUID id) {
        return ClientResponse.from(findEntity(id));
    }

    @Transactional
    public ClientResponse update(UUID id, ClientUpdateRequest request) {
        Client client = findEntity(id);
        client.update(request.name().trim(), request.active());
        return ClientResponse.from(client);
    }

    @Transactional
    public void changeOwnPassword(UUID id, PasswordChangeRequest request) {
        Client client = findEntity(id);
        if (request.currentPassword() == null
                || !passwordEncoder.matches(request.currentPassword(), client.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is invalid");
        }
        client.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void resetPassword(UUID id, PasswordChangeRequest request) {
        Client client = findEntity(id);
        client.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    private Client findEntity(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Client not found"));
    }
}
