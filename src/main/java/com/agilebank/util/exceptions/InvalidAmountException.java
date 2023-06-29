package com.agilebank.util.exceptions;

public class InvalidAmountException extends  RuntimeException{
    public InvalidAmountException(Long amount){
        super("The amount of " + amount + " is invalid; please use a non-negative amount.");
    }
}
