package ru.hse.server.utils;

import lombok.Value;

@Value
public class ValidationResult {
    boolean isValid;
    String errorMessage;

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    public boolean notValid() {
        return !isValid;
    }
}
