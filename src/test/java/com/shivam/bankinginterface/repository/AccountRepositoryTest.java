package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AccountRepositoryTest {
    @Autowired AccountRepository accountRepository;

    @Test
    void findsExistingAccount() {
        Optional<Account> result = accountRepository.findByAccountNumber("ACC1000011");

        assertTrue(result.isPresent());
        assertEquals(0, result.get().balance().compareTo(new BigDecimal("10000.00")));
    }
    @Test
    void returnsEmptyForUnknownAccount() {
        Optional<Account> result = accountRepository.findByAccountNumber("Ac1");

        assertTrue(result.isEmpty());
    }
    @Test

    void addsToBalance() {
        int rows = accountRepository.addToBalance(1L, new BigDecimal("500"));

        assertEquals(1, rows);
        Account updated = accountRepository.findByAccountNumber("ACC1000001").get();
        assertEquals(0, updated.balance().compareTo(new BigDecimal("1500.00")));
    }
    @Test
    void unknownAccountChangesNothing() {
        int rows = accountRepository.addToBalance(999L, new BigDecimal("10"));

        assertEquals(0, rows);
    }
    @Test
    void overdraftIsRefused() {
        UncategorizedSQLException e = assertThrows(UncategorizedSQLException.class,
                () -> accountRepository.addToBalance(1L, new BigDecimal("-5000")));

        assertEquals(3819, e.getSQLException().getErrorCode());
    }
}
