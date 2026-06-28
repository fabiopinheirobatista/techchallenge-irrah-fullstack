package com.br.desafioirrahbackend.repository;
import com.br.desafioirrahbackend.domain.FinancialTransaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {
 Page<FinancialTransaction> findByClientId(UUID clientId, Pageable pageable);
}
