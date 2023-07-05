package com.agilebank.util.exceptions;

import lombok.Getter;


/**
 * A {@link RuntimeException} thrown when a user attempts to register or even authenticate with a password whose length
 * is less than 8 or more than 30 characters.
 * @author jason
 */
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
