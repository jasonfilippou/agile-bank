package com.agilebank.util.exceptions;

public class BadPasswordLengthException extends RuntimeException {

  public BadPasswordLengthException(int lowerInclusive, int higherInclusive) {
    super(
        "Password should be between "
            + lowerInclusive
            + " and "
            + higherInclusive
            + " characters.");
  }
}
