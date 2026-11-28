package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.*;
import com.shivam.bankinginterface.model.*;
import com.shivam.bankinginterface.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionExecutor {
    private final AccountRepository accounts;
    private final BankTransactionRepository transactions;
    private final TransactionAuditRepository audits;

    public TransactionExecutor(AccountRepository accounts, BankTransactionRepository transactions,
                               TransactionAuditRepository audits) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.audits = audits;
    }

    @Transactional
    public void execute(TransactionRequest r) {
        long start = System.nanoTime();
        switch (r.type()) {
            case "DEPOSIT" -> deposit(r);
            case "WITHDRAWAL" -> withdraw(r);
            default -> throw new IllegalStateException("Transfer is not built yet");
        }
        transactions.insert(new BankTransaction(r.transactionId(), r.type(), r.channel(),
                r.sourceAccount(), r.destinationAccount(), r.amount(), "SUCCESS"));
        long ms = (System.nanoTime() - start) / 1_000_000;
        audits.insert(new TransactionAudit(r.transactionId(), "SUCCESS", Thread.currentThread().getName(), ms));
    }
    private Account lockActiveAccount(String accountNumber) {
        long id = accounts.findByAccountNumber(accountNumber)
                .orElseThrow(AccountNotFoundException::new)
                .accountId();
        Account locked = accounts.findByIdForUpdate(id)
                .orElseThrow(AccountNotFoundException::new);
        if (!locked.status().equals("ACTIVE")) {
            throw new AccountNotActiveException("Account is " + locked.status().toLowerCase());
        }
        return locked;
    }

    private void deposit(TransactionRequest r) {
        Account account = lockActiveAccount(r.destinationAccount());
        accounts.addToBalance(account.accountId(), r.amount());
    }

    private void withdraw(TransactionRequest r) {
        Account account = lockActiveAccount(r.sourceAccount());
        if (account.balance().compareTo(r.amount()) < 0) {
            throw new InsufficientBalanceException();
        }
        accounts.addToBalance(account.accountId(), r.amount().negate());
    }

}