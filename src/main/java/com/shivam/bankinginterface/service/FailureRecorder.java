package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.ErrorCategory;
import com.shivam.bankinginterface.model.*;
import com.shivam.bankinginterface.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FailureRecorder {
    private final BankTransactionRepository transactions;
    private final FailedTransactionRepository failures;
    private final TransactionAuditRepository audits;

    public FailureRecorder(BankTransactionRepository transactions, FailedTransactionRepository failures,
                           TransactionAuditRepository audits) {
        this.transactions = transactions;
        this.failures = failures;
        this.audits = audits;
    }

    @Transactional
    public void record(TransactionRequest r, String status, ErrorCategory category, String reason, long durationMs) {
        transactions.insert(new BankTransaction(r.transactionId(), r.type(), r.channel(), r.sourceAccount(), r.destinationAccount(), r.amount(), status));
        failures.insert(new FailedTransaction(r.transactionId(), category.name(), reason));
        audits.insert(new TransactionAudit(r.transactionId(), status, Thread.currentThread().getName(), durationMs));
    }

    @Transactional
    public void recordDuplicate(String transactionId, long durationMs) {
        audits.insert(new TransactionAudit(transactionId, "DUPLICATE", Thread.currentThread().getName(), durationMs));
    }
}