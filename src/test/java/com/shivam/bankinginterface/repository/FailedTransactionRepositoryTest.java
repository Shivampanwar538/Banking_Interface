package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.BankTransaction;
import com.shivam.bankinginterface.model.FailedTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class FailedTransactionRepositoryTest {

    @Autowired FailedTransactionRepository failedRepository;
    @Autowired BankTransactionRepository transactionRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    private void insertRejectedTransaction(String id) {
        transactionRepository.insert(new BankTransaction(id, "WITHDRAWAL", "ATM",
                "ACC1000001", null, new BigDecimal("99999.00"), "REJECTED"));
    }

    @Test
    void savesFailureForExistingTransaction() {
        insertRejectedTransaction("TXN-F1");

        failedRepository.insert(new FailedTransaction("TXN-F1", "INSUFFICIENT_BALANCE", "Insufficient balance"));

        Integer rows = jdbcTemplate.queryForObject(
                "select count(*) from failed_transaction where transaction_id = ?", Integer.class, "TXN-F1");
        assertEquals(1, rows);
    }

    @Test
    void secondFailureForSameTransactionIsRefused() {
        insertRejectedTransaction("TXN-F2");
        FailedTransaction failure = new FailedTransaction("TXN-F2", "INSUFFICIENT_BALANCE", "Insufficient balance");
        failedRepository.insert(failure);

        assertThrows(DuplicateKeyException.class, () -> failedRepository.insert(failure));
    }
}