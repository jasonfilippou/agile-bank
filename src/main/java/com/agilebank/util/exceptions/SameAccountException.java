package com.agilebank.util.exceptions;

public class SameAccountException extends RuntimeException{

    public SameAccountException(String accountId){
        super("Attempted a transaction from and to the same account with id: " + accountId);
    }
}
