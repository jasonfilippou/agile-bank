package com.agilebank.util.exceptions;

import com.agilebank.model.currency.Currency;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {

  private final Long accountId;
  private final BigDecimal accountBalanceInCurrency;
  private final Currency currency;

  public InsufficientBalanceException(
      Long accountId,
      BigDecimal accountBalanceInCurrency,
      Currency currency,
      BigDecimal amountRequested) {
    super(
        "Account "
            + accountId
            + " has a balance of "
            + accountBalanceInCurrency
            + " in currency "
            + currency
            + ", but "
            + amountRequested
            + " was requested.");
    this.accountId = accountId;
    this.accountBalanceInCurrency = accountBalanceInCurrency;
    this.currency = currency;
  }
}
