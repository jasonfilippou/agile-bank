package com.agilebank.model.account;

import com.agilebank.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDto {
    private String id;
    private Long balance;
    private Currency currency;
}
