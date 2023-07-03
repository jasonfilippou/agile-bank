package com.agilebank.util.exceptions;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvalidBalanceException extends RuntimeException{

    private final BigDecimal balance;

    public InvalidBalanceException(BigDecimal balance){
        super("Account cannot be created with non-positive balance: " + balance);
        this.balance = balance;
    }
}
