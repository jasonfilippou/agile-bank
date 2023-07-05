package com.agilebank.util.exceptions;

import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when an account with a given ID cannot be found in the DB.
 * @author jason
 */
@Getter
public class AccountNotFoundException extends RuntimeException {

  private final Long accountId;

  public AccountNotFoundException(Long accountId) {
    super("Could not find account with id: " + accountId + ".");
    this.accountId = accountId;
  }
}
