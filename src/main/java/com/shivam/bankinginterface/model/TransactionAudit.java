package com.shivam.bankinginterface.model;

public record TransactionAudit(String transactionId, String outcome, String threadName, long durationMs) {
}
