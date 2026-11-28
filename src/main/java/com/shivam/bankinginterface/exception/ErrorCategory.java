package com.shivam.bankinginterface.exception;

public enum ErrorCategory {
    VALIDATION ("Invalid Transaction"),
    ACCOUNT_NOT_FOUND("Account not found"),
    ACCOUNT_NOT_ACTIVE("Account not active"),
    INSUFFICIENT_BALANCE("Insufficient balance"),
    DUPLICATE_TRANSACTION("Duplicate transaction"),
    SYSTEM_ERROR("System error");

    private final String defaultMessage;

    ErrorCategory(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
