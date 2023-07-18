package com.agilebank.unit.service.transaction;

import static com.agilebank.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.agilebank.model.account.Account;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger.CurrencyPair;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.service.transaction.TransactionSanityChecker;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;

/**
 * Test class for {@link TransactionSanityChecker}.
 *
 * @author jason
 * @see TransactionSanityChecker
 */
public class TransactionSanityCheckerUnitTests {
  private final TransactionSanityChecker transactionSanityChecker = new TransactionSanityChecker();

  // Unhappy paths first.
  @Test(expected = AccountNotFoundException.class)
  public void whenSourceAccountIsNonExistent_thenNonExistentAccountExceptionIsThrown() {
    transactionSanityChecker.checkTransaction(
        TEST_TRANSACTIONS_INVOLVING_NON_EXISTENT_ACCOUNTS.get(0),
        Optional.empty(),
        Optional.of(TEST_ACCOUNT_ENTITIES.get(0)),
        TEST_EXCHANGE_RATES);
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenTargetAccountIsNonExistent_thenNonExistentAccountExceptionIsThrown() {
    transactionSanityChecker.checkTransaction(
        TEST_TRANSACTIONS_INVOLVING_NON_EXISTENT_ACCOUNTS.get(1),
        Optional.of(TEST_ACCOUNT_ENTITIES.get(2)),
        Optional.empty(),
        TEST_EXCHANGE_RATES);
  }

  @Test(expected = InvalidTransactionCurrencyException.class)
  public void
      whenTransactionCurrencyIsNotTargetAccountCurrency_thenInvalidTransactionCurrencyExceptionIsThrown() {
    TransactionDto transactionWithInvalidCurrency =
        TEST_TRANSACTIONS_WITH_INVALID_CURRENCIES.get(0);
    Optional<Account> sourceAccountEntityOfTransaction =
        TEST_ACCOUNT_ENTITIES.stream()
            .filter(
                account ->
                    account.getId().equals(transactionWithInvalidCurrency.getSourceAccountId()))
            .findFirst();
    Optional<Account> targetAccountEntityOfTransaction =
        TEST_ACCOUNT_ENTITIES.stream()
            .filter(
                account ->
                    account.getId().equals(transactionWithInvalidCurrency.getTargetAccountId()))
            .findFirst();
    transactionSanityChecker.checkTransaction(
        transactionWithInvalidCurrency,
        sourceAccountEntityOfTransaction,
        targetAccountEntityOfTransaction,
        TEST_EXCHANGE_RATES);
  }

  @Test(expected = SameAccountException.class)
  public void whenTransactionIsFromAnAccountToItself_thenSameAccountExceptionIsThrown() {
    TransactionDto transactionFromAccountToItself = TEST_TRANSACTIONS_FROM_ACCOUNT_TO_ITSELF.get(0);
    Optional<Account> sourceAccountEntityOfTransaction =
        TEST_ACCOUNT_ENTITIES.stream()
            .filter(
                account ->
                    account.getId().equals(transactionFromAccountToItself.getSourceAccountId()))
            .findFirst();
    Optional<Account> targetAccountEntityOfTransaction =
        TEST_ACCOUNT_ENTITIES.stream()
            .filter(
                account ->
                    account.getId().equals(transactionFromAccountToItself.getTargetAccountId()))
            .findFirst();
    transactionSanityChecker.checkTransaction(
        transactionFromAccountToItself,
        sourceAccountEntityOfTransaction,
        targetAccountEntityOfTransaction,
        TEST_EXCHANGE_RATES);
  }

  @Test(expected = InsufficientBalanceException.class)
  public void
      whenTransactionAmountIsTooMuchForSourceAccount_thenInsufficientBalanceExceptionIsThrown() {
    final Currency sourceCurrency = Currency.AFA;
    final Currency targetCurrency = Currency.AMD;
    Map<CurrencyPair, BigDecimal> singletonExchangeRate =
        Map.of(CurrencyPair.of(sourceCurrency, targetCurrency), new BigDecimal("10"));
    Account sourceAccount =
        Account.builder().id(1L).balance(new BigDecimal("2")).currency(sourceCurrency).build();
    Account targetAccount =
        Account.builder().id(2L).balance(new BigDecimal("2")).currency(targetCurrency).build();
    TransactionDto transactionDto =
        TransactionDto.builder()
            .sourceAccountId(sourceAccount.getId())
            .targetAccountId(targetAccount.getId())
            .amount(new BigDecimal("10"))
            .currency(targetCurrency)
            .build();
    transactionSanityChecker.checkTransaction(
        transactionDto,
        Optional.of(sourceAccount),
        Optional.of(targetAccount),
        singletonExchangeRate);
  }

  // Happy path last.
  @Test
  public void whenTransactionIsOk_thenOk() {
    TransactionDto transactionDto = TEST_VALID_TRANSACTION_DTOS.get(0);
    Optional<Account> sourceAccountEntityOfTransaction =
        TEST_ACCOUNT_ENTITIES.stream()
            .filter(account -> account.getId().equals(transactionDto.getSourceAccountId()))
            .findFirst();
    Optional<Account> targetAccountEntityOfTransaction =
        TEST_ACCOUNT_ENTITIES.stream()
            .filter(account -> account.getId().equals(transactionDto.getTargetAccountId()))
            .findFirst();
    Throwable expected = null;
    try {
      transactionSanityChecker.checkTransaction(
          transactionDto,
          sourceAccountEntityOfTransaction,
          targetAccountEntityOfTransaction,
          TEST_EXCHANGE_RATES);
    } catch (Throwable thrown) {
      expected = thrown;
    }
    assertNull(expected, "Did not expect sanity checker to throw any exceptions.");
  }
}
