package com.agilebank.util.exceptions;

public class OneOfTwoCurrenciesMissingException extends RuntimeException {
  public OneOfTwoCurrenciesMissingException() {
    super("One of two currencies is missing.");
  }
}
