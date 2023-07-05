package com.agilebank.util.exceptions;

/**
 * A {@link RuntimeException} thrown when a user attempts to use the {@link com.agilebank.controller.CurrencyLedgerController}'s GET endpoint
 * with only one of the two request parameters set.
 * @author jason
 */
public class OneOfTwoCurrenciesMissingException extends RuntimeException {
  public OneOfTwoCurrenciesMissingException() {
    super("One of two currencies is missing.");
  }
}
