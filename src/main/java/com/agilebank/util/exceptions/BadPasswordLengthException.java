package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class BadPasswordLengthException extends RuntimeException {

  private final Integer lowerInclusive;
  private final Integer higherInclusive;

  public BadPasswordLengthException(Integer lowerInclusive, Integer higherInclusive) {
    super(
        "Password should be between "
            + lowerInclusive
            + " and "
            + higherInclusive
            + " characters.");
    this.lowerInclusive = lowerInclusive;
    this.higherInclusive = higherInclusive;
  }
}
