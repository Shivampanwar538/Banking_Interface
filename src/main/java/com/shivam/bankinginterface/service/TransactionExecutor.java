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
            case "TRANSFER" -> transfer(r);
            default -> throw new IllegalStateException("Unsupported transaction type");
        }

        transactions.insert(new BankTransaction(r.transactionId(), r.type(), r.channel(),
                r.sourceAccount(), r.destinationAccount(), r.amount(), "SUCCESS"));
        long ms = (System.nanoTime() - start) / 1_000_000;
        audits.insert(new TransactionAudit(r.transactionId(), "SUCCESS", Thread.currentThread().getName(), ms));
    }

    private long idOf(String accountNumber) {
        return accounts.findByAccountNumber(accountNumber)
                .orElseThrow(AccountNotFoundException::new)
                .accountId();
    }

    private Account lockActive(long accountId) {
        Account locked = accounts.findByIdForUpdate(accountId)
                .orElseThrow(AccountNotFoundException::new);
        if (!locked.status().equals("ACTIVE")) {
            throw new AccountNotActiveException("Account is " + locked.status().toLowerCase());
        }
        return locked;
    }

    private Account lockActiveAccount(String accountNumber) {
        return lockActive(idOf(accountNumber));
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

    private void transfer(TransactionRequest r) {
        long sourceId = idOf(r.sourceAccount());
        long destinationId = idOf(r.destinationAccount());

        if (sourceId == destinationId) {
            throw new InvalidTransactionException("Source and destination accounts must differ");
        }

        Account first = lockActive(Math.min(sourceId, destinationId));
        Account second = lockActive(Math.max(sourceId, destinationId));
        Account source = first.accountId() == sourceId ? first : second;

        if (source.balance().compareTo(r.amount()) < 0) {
            throw new InsufficientBalanceException();
        }
        accounts.addToBalance(source.accountId(), r.amount().negate());
        accounts.addToBalance(destinationId, r.amount());
    }

}