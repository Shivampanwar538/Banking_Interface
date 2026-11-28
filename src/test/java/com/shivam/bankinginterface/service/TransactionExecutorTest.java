package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.*;
import com.shivam.bankinginterface.model.TransactionRequest;
import com.shivam.bankinginterface.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TransactionExecutorTest {
    @Autowired TransactionExecutor executor;
    @Autowired AccountRepository accounts;
    @Autowired BankTransactionRepository transactions;

    private TransactionRequest req(String id, String type, String src, String dst, String amount) {
        return new TransactionRequest(id, src, dst, new BigDecimal(amount), type, "ATM");
    }

    private BigDecimal balanceOf(String number) {
        return accounts.findByAccountNumber(number).orElseThrow().balance();
    }

    @Test
    void depositAddsMoneyAndRecordsTransaction() {
        executor.execute(req("TXN-E1", "DEPOSIT", null, "ACC1000001", "500"));

        assertEquals(0, balanceOf("ACC1000001").compareTo(new BigDecimal("1500.00")));
        assertTrue(transactions.existsByTransactionId("TXN-E1"));
    }

    @Test
    void withdrawalSubtractsMoney() {
        executor.execute(req("TXN-E2", "WITHDRAWAL", "ACC1000001", null, "400"));

        assertEquals(0, balanceOf("ACC1000001").compareTo(new BigDecimal("600.00")));
    }

    @Test
    void withdrawalBeyondBalanceIsRejected() {
        assertThrows(InsufficientBalanceException.class,
                () -> executor.execute(req("TXN-E3", "WITHDRAWAL", "ACC1000001", null, "1000.01")));
    }

    @Test
    void unknownAccountIsRejected() {
        assertThrows(AccountNotFoundException.class,
                () -> executor.execute(req("TXN-E4", "DEPOSIT", null, "NOPE", "10")));
    }

    @Test
    void frozenAccountIsRejected() {
        AccountNotActiveException e = assertThrows(AccountNotActiveException.class,
                () -> executor.execute(req("TXN-E5", "DEPOSIT", null, "ACC3000002", "10")));

        assertEquals("Account is frozen", e.getMessage());
    }
    @Test
    void transferMovesMoneyBetweenAccounts() {
        executor.execute(req("TXN-E6", "TRANSFER", "ACC1000001", "ACC1000002", "300"));

        assertEquals(0, balanceOf("ACC1000001").compareTo(new BigDecimal("700.00")));
        assertEquals(0, balanceOf("ACC1000002").compareTo(new BigDecimal("1800.00")));
    }

    @Test
    void transferBeyondBalanceIsRejectedAndChangesNothing() {
        assertThrows(InsufficientBalanceException.class,
                () -> executor.execute(req("TXN-E7", "TRANSFER", "ACC1000001", "ACC1000002", "1000.01")));

        assertEquals(0, balanceOf("ACC1000001").compareTo(new BigDecimal("1000.00")));
        assertEquals(0, balanceOf("ACC1000002").compareTo(new BigDecimal("1500.00")));
    }

    @Test
    void transferToFrozenAccountIsRejected() {
        assertThrows(AccountNotActiveException.class,
                () -> executor.execute(req("TXN-E8", "TRANSFER", "ACC1000001", "ACC3000002", "10")));

        assertEquals(0, balanceOf("ACC1000001").compareTo(new BigDecimal("1000.00")));
    }

    @Test
    void transferFromUnknownAccountIsRejected() {
        assertThrows(AccountNotFoundException.class,
                () -> executor.execute(req("TXN-E9", "TRANSFER", "NOPE", "ACC1000002", "10")));
    }
}