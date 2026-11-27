package com.shivam.bankinginterface.repository;

import com.shivam.bankinginterface.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest

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
}
