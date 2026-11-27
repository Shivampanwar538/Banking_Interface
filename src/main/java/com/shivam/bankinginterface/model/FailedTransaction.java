package com.shivam.bankinginterface.model;

public record FailedTransaction(String transactionId, String errorCategory, String reason) {
}
