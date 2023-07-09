package com.agilebank.util.exceptions;

import com.agilebank.model.currency.Currency;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when the source account of a transaction that was just POST-ed does not have a balance that can cover
 * the transaction's amount, in the transaction's currency.
 * @author jason
 */
@Getter
public class InsufficientBalanceException extends RuntimeException {

  private final Long accountId;
  private final BigDecimal accountBalanceInCurrency;
  private final Currency currency;

  public InsufficientBalanceException(
      Long accountId,
      BigDecimal accountBalanceInCurrency,
      Currency currency,
      BigDecimal amountRequestedInSourceCurrency) {
    super(
        "Account "
            + accountId
            + " has a balance of "
            + accountBalanceInCurrency
            + " in currency "
            + currency
            + ", but "
            + amountRequestedInSourceCurrency
            + " was requested.");
    this.accountId = accountId;
    this.accountBalanceInCurrency = accountBalanceInCurrency;
    this.currency = currency;
  }
}
