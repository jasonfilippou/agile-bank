package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {

  private final Long accountId;

  public AccountNotFoundException(Long accountId) {
    super("Could not find account with id: " + accountId + ".");
    this.accountId = accountId;
  }
}
