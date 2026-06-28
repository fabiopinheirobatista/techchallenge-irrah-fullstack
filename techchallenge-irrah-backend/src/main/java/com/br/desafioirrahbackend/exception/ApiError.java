package com.br.desafioirrahbackend.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldValidationError> violations
) {

    public ApiError {
        violations = violations == null ? List.of() : List.copyOf(violations);
    }
}
