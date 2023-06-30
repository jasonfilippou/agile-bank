package com.agilebank.util.exceptions;

import lombok.Getter;
import com.agilebank.model.currency.Currency;

import java.math.BigDecimal;

@Getter
public class InsufficientBalanceException extends RuntimeException {

  private final String accountId;
  private final BigDecimal accountBalanceInCurrency;
  private final Currency currency;

  public InsufficientBalanceException(String accountId, BigDecimal accountBalanceInCurrency, Currency currency, BigDecimal amountRequested) {
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
