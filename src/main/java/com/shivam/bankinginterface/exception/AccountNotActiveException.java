package com.shivam.bankinginterface.exception;

public class AccountNotActiveException extends BankException {
    public AccountNotActiveException(String message){
        super(ErrorCategory.ACCOUNT_NOT_ACTIVE, message);
    }
}
