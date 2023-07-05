package com.agilebank.util.exceptions;

import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when a transaction from an account to itself is POST-ed.
 * @author jason
 */
@Getter
public class SameAccountException extends RuntimeException {

  private final Long accountId;

  public SameAccountException(Long accountId) {
    super("Attempted a transaction from and to the same account with id: " + accountId + ".");
    this.accountId = accountId;
  }
}
