package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.InvalidTransactionException;
import com.shivam.bankinginterface.model.TransactionRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class TransactionValidator {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000000.00");
    private static boolean blank(String s) { return s == null || s.isBlank(); }
    private static final Set<String> CHANNELS = Set.of("ATM", "INTERNET", "BRANCH", "MOBILE", "UPI");

    public void validate(TransactionRequest tr) {
        String type = tr.type();
        if (type == null || !(type.equals("DEPOSIT") || type.equals("WITHDRAWAL") || type.equals("TRANSFER"))) {
            throw new InvalidTransactionException("Invalid transaction type");
        }
        if (tr.channel() == null || !CHANNELS.contains(tr.channel())) {
            throw new InvalidTransactionException("Invalid channel");
        }
        if (tr.amount() == null || tr.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Invalid amount");
        }
        if (tr.amount().stripTrailingZeros().scale() > 2){
            throw new InvalidTransactionException("Invalid amount");
        }
        if (tr.amount().compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidTransactionException("Amount exceeds limit");
        }
        if (type.equals("DEPOSIT") && blank(tr.destinationAccount())) {
            throw new InvalidTransactionException("Missing destination account");
        }
        if (type.equals("WITHDRAWAL") && blank(tr.sourceAccount())) {
            throw new InvalidTransactionException("Missing source account");
        }
        if (type.equals("TRANSFER")) {
            if (blank(tr.sourceAccount()) || blank(tr.destinationAccount())) {
                throw new InvalidTransactionException("Missing source or destination account");
            }
            if (tr.sourceAccount().equalsIgnoreCase(tr.destinationAccount())) {
                throw new InvalidTransactionException("Source and destination accounts must differ");
            }
        }
    }
}