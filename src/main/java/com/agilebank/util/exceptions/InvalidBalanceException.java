package com.agilebank.util.exceptions;

import java.math.BigDecimal;
import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when an account with a non-positive balance is POST-ed.
 * @author jason
 */
@Getter
public class InvalidBalanceException extends RuntimeException {

  private final BigDecimal balance;

  public InvalidBalanceException(BigDecimal balance) {
    super("Account cannot be created with non-positive balance: " + balance + ".");
    this.balance = balance;
  }
}
