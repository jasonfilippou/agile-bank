package com.agilebank.service.transaction;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.account.Account;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TransactionSanityChecker {

  public void checkTransaction(
      TransactionDto transactionDto,
      Optional<Account> sourceAccount,
      Optional<Account> targetAccount,
      Map<CurrencyPair, BigDecimal> currencyExchangeRates)
      throws AccountNotFoundException,
          InvalidAmountException,
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
    // If the amount of the transaction was non-positive, throw an exception.
    if (transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidAmountException(transactionDto.getAmount());
    }
    // A transaction in a currency different from the target account's is considered invalid.
    if (transactionDto.getCurrency() != targetAccount.get().getCurrency()) {
      throw new InvalidTransactionCurrencyException(
          transactionDto.getCurrency(), targetAccount.get().getCurrency());
    }
    // If you try to send an amount of currency that you can't sustain, throw an exception.
    BigDecimal sourceToTransactionCurrencyExchangeRate =
        currencyExchangeRates.get(
            new CurrencyPair(sourceAccount.get().getCurrency(), transactionDto.getCurrency()));
    // Have to divide to find out how many units of the transaction's currency we hold.
    if (transactionDto
            .getAmount()
            .compareTo(
                sourceAccount.get().getBalance().multiply(sourceToTransactionCurrencyExchangeRate))
        > 0) {
      throw new InsufficientBalanceException(
          transactionDto.getSourceAccountId(),
          sourceAccount.get().getBalance(),
          sourceAccount.get().getCurrency(),
          transactionDto.getAmount());
    }
  }
}
