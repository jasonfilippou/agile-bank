package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class SameAccountException extends RuntimeException {

  private final Long accountId;

  public SameAccountException(Long accountId) {
    super("Attempted a transaction from and to the same account with id: " + accountId);
    this.accountId = accountId;
  }
}
