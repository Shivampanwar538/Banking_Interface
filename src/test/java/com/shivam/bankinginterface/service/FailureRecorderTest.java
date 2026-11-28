package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.ErrorCategory;
import com.shivam.bankinginterface.model.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FailureRecorderTest {
    @Autowired FailureRecorder recorder;
    @Autowired JdbcTemplate jdbc;

    private TransactionRequest withdrawal(String id) {
        return new TransactionRequest(id, "ACC1000001", null, new BigDecimal("99999.00"), "WITHDRAWAL", "ATM");
    }

    private int count(String table, String id) {
        return jdbc.queryForObject("select count(*) from " + table + " where transaction_id = ?", Integer.class, id);
    }

    private void rejectInsufficient(String id) {
        recorder.record(withdrawal(id), "REJECTED", ErrorCategory.INSUFFICIENT_BALANCE, "Insufficient balance", 5);
    }

    @Test
    void rejectionWritesTransactionFailureAndAuditRows() {
        rejectInsufficient("TXN-R1");

        assertEquals(1, count("bank_transaction", "TXN-R1"));
        assertEquals(1, count("failed_transaction", "TXN-R1"));
        assertEquals(1, count("transaction_audit", "TXN-R1"));
    }

    @Test
    void failureRowKeepsCategoryAndReason() {
        rejectInsufficient("TXN-R2");

        String category = jdbc.queryForObject(
                "select error_category from failed_transaction where transaction_id = ?", String.class, "TXN-R2");
        assertEquals("INSUFFICIENT_BALANCE", category);
    }

    @Test
    void duplicateAttemptAddsOnlyAnAuditRow() {
        rejectInsufficient("TXN-R3");

        recorder.recordDuplicate("TXN-R3", 2);

        assertEquals(1, count("bank_transaction", "TXN-R3"));
        assertEquals(2, count("transaction_audit", "TXN-R3"));
    }

    @Test
    void sameIdRecordedTwiceIsRefused() {
        rejectInsufficient("TXN-R4");

        assertThrows(DuplicateKeyException.class, () -> rejectInsufficient("TXN-R4"));
    }
}