package com.agilebank.model.account;

import com.agilebank.model.currency.Currency;
import java.math.BigDecimal;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountDto {
  private Long id;
  @NonNull private BigDecimal balance;
  @NonNull private Currency currency;
}
