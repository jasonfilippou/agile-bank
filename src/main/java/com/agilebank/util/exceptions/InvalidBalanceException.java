package com.agilebank.util.exceptions;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class InvalidBalanceException extends RuntimeException {

  private final BigDecimal balance;

  public InvalidBalanceException(BigDecimal balance) {
    super("Account cannot be created with non-positive balance: " + balance + ".");
    this.balance = balance;
  }
}
