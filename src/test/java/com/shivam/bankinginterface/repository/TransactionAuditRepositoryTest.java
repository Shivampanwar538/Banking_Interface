package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.BankTransaction;
import com.shivam.bankinginterface.model.TransactionAudit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TransactionAuditRepositoryTest {
    @Autowired TransactionAuditRepository auditRepository;
    @Autowired BankTransactionRepository transactionRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void savesAuditRowForExistingTransaction() {
        transactionRepository.insert(new BankTransaction("TXN-A1", "DEPOSIT", "ATM", null,
                "ACC1000001", new BigDecimal("100.00"), "SUCCESS"));

        auditRepository.insert(new TransactionAudit("TXN-A1", "SUCCESS", "main", 5));

        Integer rows = jdbcTemplate.queryForObject(
                "select count(*) from transaction_audit where transaction_id = ?", Integer.class, "TXN-A1");
        assertEquals(1, rows);
    }
    @Test
    void refusesAuditRowForUnknownTransaction() {
        assertThrows(DataAccessException.class,
                () -> auditRepository.insert(new TransactionAudit("TXN-NOPE", "SUCCESS", "main", 5)));
    }
}