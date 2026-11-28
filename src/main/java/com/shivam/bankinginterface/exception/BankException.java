package com.shivam.bankinginterface.exception;

public abstract class BankException extends RuntimeException {
    private final ErrorCategory category;

    protected BankException(ErrorCategory category) {
        this(category, category.getDefaultMessage());
    }

    protected BankException(ErrorCategory category, String message) {
        super(message);
        this.category = category;
    }
    public ErrorCategory getCategory() { return category; }
}
