package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.FailedTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FailedTransactionRepository {
    private final JdbcTemplate jdbcTemplate;
    public FailedTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void insert(FailedTransaction ft){
        String sql = "insert into failed_transaction (transaction_id, error_category, reason) values (?, ?, ?)";
        jdbcTemplate.update(sql, ft.transactionId(), ft.errorCategory(), ft.reason());
    }
}
