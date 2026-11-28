package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class AccountRepository {
    private final JdbcTemplate jdbcTemplate;
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private static final RowMapper<Account> mapper = (rs, rowNum) -> new Account(
            rs.getLong("account_id"),
            rs.getLong("customer_id"),
            rs.getLong("branch_id"),
            rs.getString("account_number"),
            rs.getBigDecimal("balance"),
            rs.getString("status"));
    public Optional<Account> findByAccountNumber(String accountNumber){
        String sql = "select account_id, customer_id, branch_id, account_number, balance, status from account where account_number = ?";
        return jdbcTemplate.query(sql, mapper, accountNumber).stream().findFirst();
    }
    public int addToBalance(long accountId , BigDecimal amount){
        String sql = "update account set balance = balance + ? where account_id = ?";
        return jdbcTemplate.update(sql, amount, accountId);
    }
    public Optional<Account> findByIdForUpdate(long accountId) {
        String sql = "SELECT account_id, customer_id, branch_id, account_number, balance, status FROM account WHERE account_id = ? FOR UPDATE";
        return jdbcTemplate.query(sql, mapper, accountId).stream().findFirst();
    }
}
