package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.DocumentType;

public final class DocumentValidator {

    private DocumentValidator() {
    }

    public static String normalizeAndValidate(String value, DocumentType type) {
        String digits = value == null ? "" : value.replaceAll("\\D", "");
        int expectedLength = type == DocumentType.CPF ? 11 : 14;
        if (digits.length() != expectedLength || digits.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("Invalid " + type);
        }
        int baseLength = type == DocumentType.CPF ? 9 : 12;
        if (checkDigit(digits, baseLength, type) != digits.charAt(baseLength) - '0'
                || checkDigit(digits, baseLength + 1, type) != digits.charAt(baseLength + 1) - '0') {
            throw new IllegalArgumentException("Invalid " + type);
        }
        return digits;
    }

    private static int checkDigit(String digits, int length, DocumentType type) {
        int sum = 0;
        for (int index = 0; index < length; index++) {
            int weight = type == DocumentType.CPF
                    ? length + 1 - index
                    : ((length - 1 - index) % 8) + 2;
            sum += (digits.charAt(index) - '0') * weight;
        }
        if (type == DocumentType.CPF) {
            int result = 11 - (sum % 11);
            return result >= 10 ? 0 : result;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
