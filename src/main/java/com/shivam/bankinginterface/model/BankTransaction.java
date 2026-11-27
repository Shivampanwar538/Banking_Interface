package com.shivam.bankinginterface.model;

import java.math.BigDecimal;

public record BankTransaction(String transactionId, String type, String channel, String sourceAccount, String destinationAccount ,BigDecimal amount, String status) {}
