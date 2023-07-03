package com.agilebank.model.transaction;

import com.agilebank.model.currency.Currency;
import java.math.BigDecimal;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class TransactionDto {
  private Long id;
  @NonNull private Long sourceAccountId;
  @NonNull private Long targetAccountId;
  @NonNull private BigDecimal amount;
  @NonNull private Currency currency;
}
