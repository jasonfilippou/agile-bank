package com.agilebank.model.account;

import com.agilebank.model.currency.Currency;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
  private String id;
  private BigDecimal balance;
  private Currency currency;
}
