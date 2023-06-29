package com.agilebank.util.exceptions;

public class InsufficientBalanceException extends RuntimeException {

  public InsufficientBalanceException(String accountId, Long accountBalance, Long amountRequested) {
    super(
        "Account "
            + accountId
            + " has a balance of "
            + accountBalance
            + ", but "
            + amountRequested
            + " was requested.");
  }
}
