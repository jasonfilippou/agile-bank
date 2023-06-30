package com.agilebank.util.exceptions;

import lombok.Getter;
import com.agilebank.model.currency.Currency;

@Getter
public class InsufficientBalanceException extends RuntimeException {

  private final String accountId;
  private final Double accountBalanceInCurrency;
  private final Currency currency;

  public InsufficientBalanceException(String accountId, Double accountBalanceInCurrency, Currency currency, Double amountRequested) {
    super(
        "Account "
            + accountId
            + " has a balance of "
            + accountBalanceInCurrency
            + " in currency " + currency
            + ", but "
            + amountRequested
            + " was requested.");
    this.accountId = accountId;
    this.accountBalanceInCurrency = accountBalanceInCurrency;
    this.currency = currency;
  }
}
