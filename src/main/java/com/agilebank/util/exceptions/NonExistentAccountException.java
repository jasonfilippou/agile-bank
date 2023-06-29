package com.agilebank.util.exceptions;

public class NonExistentAccountException extends RuntimeException{

    public NonExistentAccountException(String accountId){
        super("Account with id: " + accountId + " is non-existent.");
    }
}
