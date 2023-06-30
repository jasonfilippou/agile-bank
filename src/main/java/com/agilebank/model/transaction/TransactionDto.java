package com.agilebank.model.transaction;

import com.agilebank.model.currency.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionDto {
  @NonNull private String sourceAccountId;
  @NonNull private String targetAccountId;
  @NonNull private BigDecimal amount;
  @NonNull private Currency currency;
}
