package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class SameAccountException extends RuntimeException{

    private final String accountId;
    public SameAccountException(String accountId){
        super("Attempted a transaction from and to the same account with id: " + accountId);
        this.accountId = accountId;
    }
}
