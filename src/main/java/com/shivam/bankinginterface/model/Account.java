package com.shivam.bankinginterface.model;

import java.math.BigDecimal;

public record Account(long accountId, long customerId, long branchId, String accountNumber, BigDecimal balance, String status) {

}
