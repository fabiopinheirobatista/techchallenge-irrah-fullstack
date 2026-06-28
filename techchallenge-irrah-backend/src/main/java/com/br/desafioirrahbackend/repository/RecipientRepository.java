package com.br.desafioirrahbackend.repository;

import com.br.desafioirrahbackend.domain.ContactType;
import com.br.desafioirrahbackend.domain.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipientRepository extends JpaRepository<Recipient, UUID> {

    boolean existsByContactAndContactType(String contact, ContactType contactType);
}
