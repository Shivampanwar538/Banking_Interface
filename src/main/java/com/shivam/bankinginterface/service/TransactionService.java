package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.*;
import com.shivam.bankinginterface.model.*;
import com.shivam.bankinginterface.repository.BankTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionValidator validator;
    private final TransactionExecutor executor;
    private final FailureRecorder recorder;
    private final BankTransactionRepository transactions;

    public TransactionService(TransactionValidator validator, TransactionExecutor executor, FailureRecorder recorder, BankTransactionRepository transactions) {
        this.validator = validator;
        this.executor = executor;
        this.recorder = recorder;
        this.transactions = transactions;
    }

    // deliberately NOT @Transactional: the executor and the recorder each run their own transaction
    public TransactionResult process(TransactionRequest request) {
        long start = System.nanoTime();
        String id = request.transactionId();
        if (id == null || id.isBlank()) {
            log.warn("Rejected a request with no transaction id");
            return new TransactionResult(id, "REJECTED", "Missing transaction id");
        }
        TransactionRequest r = withChannel(request);
        if (transactions.existsByTransactionId(id)) {
            return duplicate(id, start);
        }
        try {
            validator.validate(r);
            executor.execute(r);
            return new TransactionResult(id, "SUCCESS", "Processed successfully");
        }
        catch (DuplicateKeyException e) {
            return duplicate(id, start);
        }
        catch (BankException e) {
            return recorded(r, "REJECTED", e.getCategory(), e.getMessage(), start);
        }
        catch (RuntimeException e) {
            log.error("Transaction failed unexpectedly, id={}", id, e);
            return recorded(r, "FAILED", ErrorCategory.SYSTEM_ERROR, ErrorCategory.SYSTEM_ERROR.getDefaultMessage(), start);
        }
    }

    private TransactionRequest withChannel(TransactionRequest r) {
        if (r.channel() != null && !r.channel().isBlank()) {
            return r;
        }
        return new TransactionRequest(r.transactionId(), r.sourceAccount(), r.destinationAccount(),
                r.amount(), r.type(), "INTERNET");
    }

    private TransactionResult recorded(TransactionRequest r, String status, ErrorCategory category,
                                       String reason, long start) {
        try {
            recorder.record(r, status, category, reason, elapsedMs(start));
        } catch (DuplicateKeyException e) {
            return duplicate(r.transactionId(), start);
        } catch (RuntimeException e) {
            log.warn("Could not store {} transaction id={}: {}", status, r.transactionId(), e.getClass().getSimpleName());
        }
        return new TransactionResult(r.transactionId(), status, reason);
    }

    private TransactionResult duplicate(String id, long start) {
        try {
            recorder.recordDuplicate(id, elapsedMs(start));
        } catch (RuntimeException e) {
            log.warn("Could not store duplicate audit for id={}", id);
        }
        return new TransactionResult(id, "DUPLICATE", ErrorCategory.DUPLICATE_TRANSACTION.getDefaultMessage());
    }

    private long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}