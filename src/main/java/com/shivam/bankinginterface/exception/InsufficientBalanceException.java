package com.shivam.bankinginterface.exception;

public class InsufficientBalanceException extends BankException {
    public InsufficientBalanceException() {
        super(ErrorCategory.INSUFFICIENT_BALANCE);
    }
}
