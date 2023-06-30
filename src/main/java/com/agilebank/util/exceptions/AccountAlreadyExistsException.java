package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class AccountAlreadyExistsException extends RuntimeException{
    private final String accountId;

    public AccountAlreadyExistsException(String accountId){
        super("Account with account ID " + accountId + " already exists.");
        this.accountId = accountId;
    }
}
