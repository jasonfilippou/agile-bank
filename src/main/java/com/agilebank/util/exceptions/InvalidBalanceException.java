package com.agilebank.util.exceptions;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvalidBalanceException extends RuntimeException{
    private final String accountId;
    private final BigDecimal balance;

    public InvalidBalanceException(String accountId, BigDecimal balance){
        super("Account with " + accountId + " cannot be created with non-positive balance: " + balance);
        this.accountId = accountId;
        this.balance = balance;
    }
}
