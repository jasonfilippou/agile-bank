package com.agilebank.model.account;

import com.agilebank.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class AccountDto {
    @NonNull
    private String id;
    @NonNull
    private Long balance;
    @NonNull
    private Currency currency;
}
