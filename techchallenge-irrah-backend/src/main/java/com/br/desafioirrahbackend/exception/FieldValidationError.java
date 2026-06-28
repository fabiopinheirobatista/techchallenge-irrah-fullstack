package com.br.desafioirrahbackend.exception;

public record FieldValidationError(String field, String message) {
}
