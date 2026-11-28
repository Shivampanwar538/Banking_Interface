package com.shivam.bankinginterface.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankExceptionTest {
    @Test void accountNotFound() { check(new AccountNotFoundException(), ErrorCategory.ACCOUNT_NOT_FOUND, "Account not found"); }
    @Test void insufficientBalance() { check(new InsufficientBalanceException(), ErrorCategory.INSUFFICIENT_BALANCE, "Insufficient balance"); }
    @Test void duplicate() { check(new DuplicateTransactionException(), ErrorCategory.DUPLICATE_TRANSACTION, "Duplicate transaction"); }
    @Test void invalid() { check(new InvalidTransactionException("Invalid amount"), ErrorCategory.VALIDATION, "Invalid amount"); }
    @Test void notActive() { check(new AccountNotActiveException("Account is frozen"), ErrorCategory.ACCOUNT_NOT_ACTIVE, "Account is frozen"); }

    private void check(BankException e, ErrorCategory category, String message) {
        assertEquals(category, e.getCategory());
        assertEquals(message, e.getMessage());
    }
}