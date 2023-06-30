package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class InvalidAmountException extends RuntimeException {

  private final Double amount;

  public InvalidAmountException(Double amount) {
    super("The amount of " + amount + " is invalid; please use a non-negative amount.");
    this.amount = amount;
  }
}
