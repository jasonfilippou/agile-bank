package com.agilebank.util.exceptions;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class InvalidAmountException extends RuntimeException {

  private final BigDecimal amount;

  public InvalidAmountException(BigDecimal amount) {
    super("The amount of " + amount + " is invalid; please use a non-negative amount.");
    this.amount = amount;
  }
}
