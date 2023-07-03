package com.agilebank.unit.service.transaction;

import com.agilebank.service.transaction.TransactionSanityChecker;
import com.agilebank.util.exceptions.*;
import org.junit.Test;

import java.util.Optional;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TransactionSanityCheckerUnitTests {
    private final TransactionSanityChecker transactionSanityChecker = new TransactionSanityChecker();

    // Unhappy paths first.
    @Test(expected = AccountNotFoundException.class)
    public void whenSourceAccountIsNonExistent_thenNonExistentAccountExceptionIsThrown(){
    transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_ONE, Optional.empty(),
            Optional.of(TEST_ACCOUNT_ONE), TEST_EXCHANGE_RATES);
    }

    @Test(expected = AccountNotFoundException.class)
    public void whenTargetAccountIsNonExistent_thenNonExistentAccountExceptionIsThrown(){
        transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_ONE, Optional.of(TEST_ACCOUNT_THREE),
                Optional.empty(), TEST_EXCHANGE_RATES);
    }

    @Test(expected = InvalidAmountException.class)
    public void whenTransactionIsOverANonPositiveAmountOfCurrency_thenInvalidAmountExceptionIsThrown(){
        // Transaction 3 is over zero USD.
        transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_THREE, Optional.of(TEST_ACCOUNT_ONE),
                Optional.of(TEST_ACCOUNT_THREE), TEST_EXCHANGE_RATES);
    }

    @Test(expected = InvalidTransactionCurrencyException.class)
    public void whenTransactionCurrencyIsNotTargetAccountCurrency_thenInvalidTransactionCurrencyExceptionIsThrown(){
        // Transaction 4 reproduces this scenario.
        transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_FOUR, Optional.of(TEST_ACCOUNT_THREE),
                Optional.of(TEST_ACCOUNT_TWO), TEST_EXCHANGE_RATES);
    }

    @Test(expected = SameAccountException.class)
    public void whenTransactionIsFromAnAccountToItself_thenSameAccountExceptionIsThrown(){
        // Transaction 5 is from account 2 to account 2.
        transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_FIVE, Optional.of(TEST_ACCOUNT_TWO),
                Optional.of(TEST_ACCOUNT_TWO), TEST_EXCHANGE_RATES);
    }

    @Test(expected = InsufficientBalanceException.class)
    public void whenTransactionAmountIsTooMuchForSourceAccount_thenInsufficientBalanceExceptionIsThrown(){
        // Transaction 2 is too much for account 1 given the test exchange rate between the currencies.
        transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_TWO, Optional.of(TEST_ACCOUNT_ONE),
                Optional.of(TEST_ACCOUNT_TWO), TEST_EXCHANGE_RATES);
    }

    // Happy path last.
    @Test
    public void whenTransactionIsOk_thenOk(){
        // Transaction 1 is perfectly doable.
        Throwable expected = null;
        try {
            transactionSanityChecker.checkTransaction(TEST_TRANSACTION_DTO_ONE, Optional.of(TEST_ACCOUNT_ONE),
                    Optional.of(TEST_ACCOUNT_TWO), TEST_EXCHANGE_RATES);
        } catch(Throwable thrown){
            expected = thrown;
        }
        assertNull(expected, "Did not expect sanity checker to throw any exceptions.");
    }
}
