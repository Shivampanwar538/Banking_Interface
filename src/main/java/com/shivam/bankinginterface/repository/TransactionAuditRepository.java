package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.TransactionAudit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionAuditRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionAuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void insert(TransactionAudit ta) {
        String sql = "insert into transaction_audit(transaction_id, outcome, thread_name, duration_ms) values(?,?,?,?)";
        jdbcTemplate.update(sql, ta.transactionId(), ta.outcome(), ta.threadName(), ta.durationMs());
    }
}
