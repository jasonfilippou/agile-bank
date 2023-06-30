package com.agilebank.util.exceptions;

import com.agilebank.model.Currency;
import lombok.Getter;

@Getter
public class DifferentCurrenciesException extends RuntimeException{

    private final Currency sourceAccountCurrency;
    private final Currency targetAccountCurrency;
    public DifferentCurrenciesException(Currency sourceAccountCurrency, Currency targetAccountCurrency){
        this.sourceAccountCurrency = sourceAccountCurrency;
        this.targetAccountCurrency = targetAccountCurrency;
    }
}
