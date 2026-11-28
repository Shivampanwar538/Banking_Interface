package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransactionServiceTest {
    @Autowired TransactionService service;
    @Autowired JdbcTemplate jdbc;

    @AfterEach
    void cleanup() {
        jdbc.update("delete from transaction_audit where transaction_id like 'TXN-S%'");
        jdbc.update("delete from failed_transaction where transaction_id like 'TXN-S%'");
        jdbc.update("delete from bank_transaction where transaction_id like 'TXN-S%'");
        jdbc.update("update account set balance = 1000.00 where account_number = 'ACC1000001'");
    }

    private TransactionRequest deposit(String id, String account, String amount) {
        return new TransactionRequest(id, null, account, new BigDecimal(amount), "DEPOSIT", "ATM");
    }

    private int count(String table, String id) {
        return jdbc.queryForObject("select count(*) from " + table + " where transaction_id = ?", Integer.class, id);
    }

    private BigDecimal balance() {
        return jdbc.queryForObject("select balance from account where account_number = 'ACC1000001'", BigDecimal.class);
    }

    @Test
    void successfulDepositStoresAllRows() {
        TransactionResult result = service.process(deposit("TXN-S1", "ACC1000001", "100"));

        assertEquals("SUCCESS", result.status());
        assertEquals(0, balance().compareTo(new BigDecimal("1100.00")));
        assertEquals(1, count("bank_transaction", "TXN-S1"));
        assertEquals(1, count("transaction_audit", "TXN-S1"));
        assertEquals(0, count("failed_transaction", "TXN-S1"));
    }

    @Test
    void insufficientBalanceIsRejectedAndRecorded() {
        TransactionRequest r = new TransactionRequest("TXN-S2", "ACC1000001", null,
                new BigDecimal("99999"), "WITHDRAWAL", "ATM");

        TransactionResult result = service.process(r);

        assertEquals("REJECTED", result.status());
        assertEquals("Insufficient balance", result.reason());
        assertEquals(0, balance().compareTo(new BigDecimal("1000.00")));
        assertEquals(1, count("failed_transaction", "TXN-S2"));
    }

    @Test
    void frozenAccountIsRejectedWithReason() {
        TransactionResult result = service.process(deposit("TXN-S3", "ACC3000002", "10"));

        assertEquals("REJECTED", result.status());
        assertEquals("Account is frozen", result.reason());
    }

    @Test
    void negativeAmountIsRejectedAndStored() {
        TransactionResult result = service.process(deposit("TXN-S4", "ACC1000001", "-5"));

        assertEquals("REJECTED", result.status());
        assertEquals("Invalid amount", result.reason());
        assertEquals(1, count("bank_transaction", "TXN-S4"));
    }

    @Test
    void duplicateDoesNotChangeTheBalanceTwice() {
        service.process(deposit("TXN-S5", "ACC1000001", "100"));

        TransactionResult second = service.process(deposit("TXN-S5", "ACC1000001", "100"));

        assertEquals("DUPLICATE", second.status());
        assertEquals(0, balance().compareTo(new BigDecimal("1100.00")));
        assertEquals(1, count("bank_transaction", "TXN-S5"));
        assertEquals(2, count("transaction_audit", "TXN-S5"));
    }

    @Test
    void unknownTypeIsRejectedButNotStored() {
        TransactionRequest r = new TransactionRequest("TXN-S6", null, "ACC1000001",
                new BigDecimal("10"), "REFUND", "ATM");

        TransactionResult result = service.process(r);

        assertEquals("REJECTED", result.status());
        assertEquals("Invalid transaction type", result.reason());
        assertEquals(0, count("bank_transaction", "TXN-S6"));
    }

    @Test
    void missingChannelDefaultsToInternet() {
        TransactionRequest r = new TransactionRequest("TXN-S7", null, "ACC1000001",
                new BigDecimal("10"), "DEPOSIT", null);

        TransactionResult result = service.process(r);

        assertEquals("SUCCESS", result.status());
        String channel = jdbc.queryForObject(
                "select channel from bank_transaction where transaction_id = ?", String.class, "TXN-S7");
        assertEquals("INTERNET", channel);
    }
}