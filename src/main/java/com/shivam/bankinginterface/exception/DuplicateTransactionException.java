package com.shivam.bankinginterface.exception;

public class DuplicateTransactionException extends BankException {
    public DuplicateTransactionException() {
        super(ErrorCategory.DUPLICATE_TRANSACTION);
    }
}
