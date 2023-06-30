package com.agilebank.model.transaction;

import com.agilebank.model.currency.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class TransactionDto {
  @NonNull private String sourceAccountId;
  @NonNull private String targetAccountId;
  @NonNull private Long amount;
  @NonNull private Currency currency;
}
