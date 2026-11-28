package com.shivam.bankinginterface.exception;

public class InvalidTransactionException extends BankException {
    public InvalidTransactionException(String message) {
        super(ErrorCategory.VALIDATION, message);
    }
}
