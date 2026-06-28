package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Recipient;
import com.br.desafioirrahbackend.dto.RecipientRequest;
import com.br.desafioirrahbackend.dto.RecipientResponse;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;

    @Transactional(readOnly = true)
    public Page<RecipientResponse> list(Pageable pageable) {
        return recipientRepository.findAll(pageable).map(RecipientResponse::from);
    }

    @Transactional
    public RecipientResponse create(RecipientRequest request) {
        String contact = request.contact().trim();
        if (recipientRepository.existsByContactAndContactType(contact, request.contactType())) {
            throw new ApiException(HttpStatus.CONFLICT, "Recipient contact is already registered");
        }
        Recipient recipient = new Recipient(request.name().trim(), contact, request.contactType());
        return RecipientResponse.from(recipientRepository.save(recipient));
    }

    @Transactional
    public RecipientResponse update(UUID id, String name, boolean active) {
        Recipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipient not found"));
        recipient.update(name.trim(), active);
        return RecipientResponse.from(recipient);
    }
}
