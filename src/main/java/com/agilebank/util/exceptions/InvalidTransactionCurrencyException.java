package com.agilebank.util.exceptions;

import com.agilebank.model.currency.Currency;
import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when a transaction with a currency different from the target account's is POST-ed.
 * @author jason
 */
@Getter
public class InvalidTransactionCurrencyException extends RuntimeException {

  private final Currency transactionCurrency;
  private final Currency targetAccountCurrency;

  public InvalidTransactionCurrencyException(
      Currency transactionCurrency, Currency targetAccountCurrency) {
    super(
        "Invalid transaction currency "
            + transactionCurrency
            + "; target account's currency is "
            + targetAccountCurrency
            + ".");
    this.transactionCurrency = transactionCurrency;
    this.targetAccountCurrency = targetAccountCurrency;
  }
}
