package com.agilebank.util.exceptions;

import com.agilebank.model.Currency;
import lombok.Getter;

@Getter
public class InvalidTransactionCurrencyException extends RuntimeException{
    private final Currency transactionCurrency;

    public InvalidTransactionCurrencyException(Currency transactionCurrency){
        this.transactionCurrency = transactionCurrency;
    }
}
