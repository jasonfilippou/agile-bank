package com.agilebank.util.exceptions;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvalidAmountException extends RuntimeException {

  private final BigDecimal amount;

  public InvalidAmountException(BigDecimal amount) {
    super("The amount of " + amount + " is invalid; please use a non-negative amount.");
    this.amount = amount;
  }
}
