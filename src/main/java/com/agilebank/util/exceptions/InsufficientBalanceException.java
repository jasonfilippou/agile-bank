package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {

  private final String accountId;
  private final Long accountBalance;
  public InsufficientBalanceException(String accountId, Long accountBalance, Long amountRequested) {
    super(
        "Account "
            + accountId
            + " has a balance of "
            + accountBalance
            + ", but "
            + amountRequested
            + " was requested.");
    this.accountId = accountId;
    this.accountBalance = accountBalance;
  }
}
