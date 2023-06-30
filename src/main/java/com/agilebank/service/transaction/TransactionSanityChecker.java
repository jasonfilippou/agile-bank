package com.agilebank.service.transaction;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.exceptions.*;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionSanityChecker {
  
  public void checkTransaction(
      TransactionDto transactionDto,
      Optional<AccountDao> sourceAccount,
      Optional<AccountDao> targetAccount,
      Map<CurrencyPair, Double> currencyExchangeRates) {
    if (sourceAccount.isEmpty()) {
      throw new NonExistentAccountException(transactionDto.getSourceAccountId().strip());
    }
    if (targetAccount.isEmpty()) {
      throw new NonExistentAccountException(transactionDto.getTargetAccountId().strip());
    }
    if (transactionDto.getAmount() <= 0) {
      throw new InvalidAmountException(transactionDto.getAmount());
    }
    // A transaction in a currency different from the target account's is considered invalid.
    if (transactionDto.getCurrency() != targetAccount.get().getCurrency()) {
      throw new InvalidTransactionCurrencyException(
          transactionDto.getCurrency(), targetAccount.get().getCurrency());
    }
    // If you try to send an amount of currency that you can't sustain, throw an exception.
    Double sourceToTransactionCurrencyExchangeRate =
        currencyExchangeRates.get(
            new CurrencyPair(sourceAccount.get().getCurrency(), transactionDto.getCurrency()));
    if (transactionDto.getAmount()
        > sourceAccount.get().getBalance()
            / (double) sourceToTransactionCurrencyExchangeRate) {
      throw new InsufficientBalanceException(
          transactionDto.getSourceAccountId().strip(),
          sourceAccount.get().getBalance(),
          sourceAccount.get().getCurrency(),
          transactionDto.getAmount());
    }
  }
}
