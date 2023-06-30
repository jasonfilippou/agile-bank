package com.agilebank.model.account;

import com.agilebank.model.currency.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDto {
  @NonNull private String id;
  @NonNull private BigDecimal balance;
  @NonNull private Currency currency;
}
