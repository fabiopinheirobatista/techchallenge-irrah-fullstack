package com.br.desafioirrahbackend.repository;

import com.br.desafioirrahbackend.domain.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {
    Page<FinancialTransaction> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);
}
