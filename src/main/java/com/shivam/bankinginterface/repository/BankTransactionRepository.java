package com.shivam.bankinginterface.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.shivam.bankinginterface.model.BankTransaction;

@Repository
public class BankTransactionRepository {
    private final JdbcTemplate jdbcTemplate;
    public BankTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void insert(BankTransaction trn){
        String sql ="insert into bank_transaction(transaction_id, type, channel, source_account, destination_account, amount ,status ) values(?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, trn.transactionId(), trn.type(), trn.channel(), trn.sourceAccount(), trn.destinationAccount(), trn.amount(), trn.status());
    }
    public boolean existsByTransactionId(String transactionId){
        String sql ="select count(*) from bank_transaction where transaction_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, transactionId);
        return count != null && count > 0;
    }
}
