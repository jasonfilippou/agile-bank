package com.agilebank.service.transaction;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.account.Account;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * A utility class used to perform several checks on a transaction to make sure it is valid.
 * @author jason 
 * @see TransactionService
 */
@Component
public class TransactionSanityChecker {

  /**
   * Check the provided transaction to ensure that several constraints are satisfied.
   * @param transactionDto The transaction to check.
   * @param sourceAccount The source account of the transaction (might not exist).
   * @param targetAccount The target account of the transaction (might not exist).
   * @param currencyExchangeRates A {@link Map} of exchange rates for {@link com.agilebank.model.currency.Currency} pairs.
   * @throws AccountNotFoundException If either {@literal sourceAccountId} or {@literal targetAccountId} are empty {@link Optional}s (i.e do not exist in the DB).
   * @throws InvalidTransactionCurrencyException If the {@link com.agilebank.model.currency.Currency} of the transaction is different from the one used by the account {@literal targetAccount}.
   * @throws SameAccountException If {@literal sourceAccount} and {@literal targetAccount} correspond to the same account.
   * @throws InsufficientBalanceException If there is an insufficient balance in the source account (in the transaction's currency) to satisfy the amount of the transaction.
   */
  public void checkTransaction(
      TransactionDto transactionDto,
      Optional<Account> sourceAccount,
      Optional<Account> targetAccount,
      Map<CurrencyPair, BigDecimal> currencyExchangeRates)
      throws AccountNotFoundException,
          InvalidTransactionCurrencyException,
          SameAccountException,
          InsufficientBalanceException {
    // If either account could not be found, throw an exception.
    if (sourceAccount.isEmpty()) {
      throw new AccountNotFoundException(transactionDto.getSourceAccountId());
    }
    if (targetAccount.isEmpty()) {
      throw new AccountNotFoundException(transactionDto.getTargetAccountId());
    }
    if (sourceAccount.get().getId().equals(targetAccount.get().getId())) {
      throw new SameAccountException(sourceAccount.get().getId());
    }
    // A transaction in a currency different from the target account's is considered invalid.
    if (transactionDto.getCurrency() != targetAccount.get().getCurrency()) {
      throw new InvalidTransactionCurrencyException(
          transactionDto.getCurrency(), targetAccount.get().getCurrency());
    }
    // If you try to send an amount of currency that you can't sustain, throw an exception.
    BigDecimal sourceToTransactionCurrencyExchangeRate =
        currencyExchangeRates.get(
            CurrencyPair.of(sourceAccount.get().getCurrency(), transactionDto.getCurrency()));
    if (transactionDto
            .getAmount().multiply(sourceToTransactionCurrencyExchangeRate)
            .compareTo(
                sourceAccount.get().getBalance())
        > 0) {
      throw new InsufficientBalanceException(
          transactionDto.getSourceAccountId(),
          sourceAccount.get().getBalance(),
          sourceAccount.get().getCurrency(),
          transactionDto.getAmount().multiply(sourceToTransactionCurrencyExchangeRate));
    }
  }
}
