package com.br.desafioirrahbackend.repository;

import com.br.desafioirrahbackend.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByDocumentId(String documentId);

    boolean existsByDocumentId(String documentId);
}
