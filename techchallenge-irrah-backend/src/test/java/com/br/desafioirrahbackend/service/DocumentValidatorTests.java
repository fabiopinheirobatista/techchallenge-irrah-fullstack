package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.DocumentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DocumentValidatorTests {

    @Test
    void shouldNormalizeValidCpf() {
        assertThat(DocumentValidator.normalizeAndValidate("529.982.247-25", DocumentType.CPF))
                .isEqualTo("52998224725");
    }

    @Test
    void shouldNormalizeValidCnpj() {
        assertThat(DocumentValidator.normalizeAndValidate("11.444.777/0001-61", DocumentType.CNPJ))
                .isEqualTo("11444777000161");
    }

    @Test
    void shouldRejectInvalidDocument() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> DocumentValidator.normalizeAndValidate("111.111.111-11", DocumentType.CPF));
    }
}
