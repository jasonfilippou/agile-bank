package com.agilebank.util.exceptions;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException{

    private final Long id;

    public TransactionNotFoundException(Long id){
        super("Could not find transaction with id: " + id);
        this.id = id;
    }
}
