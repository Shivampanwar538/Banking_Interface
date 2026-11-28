package com.shivam.bankinginterface.service;

import com.shivam.bankinginterface.exception.InvalidTransactionException;
import com.shivam.bankinginterface.model.TransactionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {
    private final TransactionValidator validator = new TransactionValidator();

    private TransactionRequest request(String type, String src, String dst, String amount) {
        BigDecimal value = amount == null ? null : new BigDecimal(amount);
        return new TransactionRequest("TXN1", src, dst, value, type, "ATM");
    }

    private void assertRejected(TransactionRequest r, String message) {
        InvalidTransactionException e = assertThrows(InvalidTransactionException.class, () -> validator.validate(r));
        assertEquals(message, e.getMessage());
    }

    @Test void validDepositPasses() { assertDoesNotThrow(() -> validator.validate(request("DEPOSIT", null, "ACC1", "100.00"))); }
    @Test void validWithdrawalPasses() { assertDoesNotThrow(() -> validator.validate(request("WITHDRAWAL", "ACC1", null, "100"))); }
    @Test void validTransferPasses() { assertDoesNotThrow(() -> validator.validate(request("TRANSFER", "ACC1", "ACC2", "100.50"))); }

    @Test void unknownTypeRejected() { assertRejected(request("REFUND", null, "ACC1", "10"), "Invalid transaction type"); }
    @Test void zeroAmountRejected() { assertRejected(request("DEPOSIT", null, "ACC1", "0"), "Invalid amount"); }
    @Test void negativeAmountRejected() { assertRejected(request("DEPOSIT", null, "ACC1", "-5"), "Invalid amount"); }
    @Test void missingAmountRejected() { assertRejected(request("DEPOSIT", null, "ACC1", null), "Invalid amount"); }
    @Test void tooManyDecimalsRejected() { assertRejected(request("DEPOSIT", null, "ACC1", "10.005"), "Invalid amount"); }
    @Test void amountAboveLimitRejected() { assertRejected(request("DEPOSIT", null, "ACC1", "10000000.01"), "Amount exceeds limit"); }

    @Test void depositNeedsDestination() { assertRejected(request("DEPOSIT", null, null, "10"), "Missing destination account"); }
    @Test void withdrawalNeedsSource() { assertRejected(request("WITHDRAWAL", null, null, "10"), "Missing source account"); }
    @Test void transferNeedsBothAccounts() { assertRejected(request("TRANSFER", "ACC1", null, "10"), "Missing source or destination account"); }
    @Test void transferToSameAccountRejected() { assertRejected(request("TRANSFER", "ACC1", "acc1", "10"), "Source and destination accounts must differ"); }
    @Test void unknownChannelRejected() { assertRejected(new TransactionRequest("TXN1", null, "ACC1", new BigDecimal("10"), "DEPOSIT", "FAX"), "Invalid channel");}
}