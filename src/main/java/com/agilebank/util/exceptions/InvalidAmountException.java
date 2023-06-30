package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class InvalidAmountException extends RuntimeException {

  private final Long amount;

  public InvalidAmountException(Long amount) {
    super("The amount of " + amount + " is invalid; please use a non-negative amount.");
    this.amount = amount;
  }
}
