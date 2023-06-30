package com.agilebank.util.exceptions;

import com.agilebank.model.currency.Currency;
import lombok.Getter;

@Getter
public class InvalidTransactionCurrencyException extends RuntimeException {

  private final Currency transactionCurrency;
  private final Currency targetAccountCurrency;

  public InvalidTransactionCurrencyException(
      Currency transactionCurrency, Currency targetAccountCurrency) {
    this.transactionCurrency = transactionCurrency;
    this.targetAccountCurrency = targetAccountCurrency;
  }
}
