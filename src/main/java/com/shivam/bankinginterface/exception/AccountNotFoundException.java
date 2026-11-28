package com.shivam.bankinginterface.exception;

public class AccountNotFoundException extends BankException {
    public AccountNotFoundException() {
        super(ErrorCategory.ACCOUNT_NOT_FOUND);
    }
}
