package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.BankTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BankTransactionRepositoryTest {

    @Autowired
    BankTransactionRepository transactionRepository;

    private BankTransaction deposit(String id) {
        return new BankTransaction(id, "DEPOSIT", "ATM", null, "ACC1000001",
                new BigDecimal("100.00"), "SUCCESS");
    }

    @Test
    void insertedTransactionExists() {
        transactionRepository.insert(deposit("TXN-T1"));

        assertTrue(transactionRepository.existsByTransactionId("TXN-T1"));
    }

    @Test
    void unknownTransactionDoesNotExist() {
        assertFalse(transactionRepository.existsByTransactionId("NOPE"));
    }

    @Test
    void duplicateTransactionIdIsRefused() {
        transactionRepository.insert(deposit("TXN-T2"));

        assertThrows(DuplicateKeyException.class, () -> transactionRepository.insert(deposit("TXN-T2")));
    }
}
