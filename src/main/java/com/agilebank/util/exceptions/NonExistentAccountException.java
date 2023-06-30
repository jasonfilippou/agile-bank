package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class NonExistentAccountException extends RuntimeException {

  private final String accountId;

  public NonExistentAccountException(String accountId) {
    super("Account with id: " + accountId + " is non-existent.");
    this.accountId = accountId;
  }
}
