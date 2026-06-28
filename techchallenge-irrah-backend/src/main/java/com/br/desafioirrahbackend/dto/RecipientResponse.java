package com.br.desafioirrahbackend.dto;

import com.br.desafioirrahbackend.domain.ContactType;
import com.br.desafioirrahbackend.domain.Recipient;

import java.util.UUID;

public record RecipientResponse(UUID id, String name, String contact, ContactType contactType, boolean active) {
    public static RecipientResponse from(Recipient recipient) {
        return new RecipientResponse(recipient.getId(), recipient.getName(), recipient.getContact(),
                recipient.getContactType(), recipient.isActive());
    }
}
