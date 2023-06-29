package com.agilebank.model.transaction;

import com.agilebank.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {
    private String sourceAccountId;
    private String targetAccountId;
    private Long amount;
    private Currency currency;
}
