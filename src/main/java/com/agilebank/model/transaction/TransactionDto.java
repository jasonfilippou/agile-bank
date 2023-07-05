package com.agilebank.model.transaction;

import com.agilebank.model.currency.Currency;
import java.math.BigDecimal;
import lombok.*;

/**
 * Simple POJO that defines the data transfer object for transactions.
 * @author jason 
 * 
 * @see Transaction
 * @see com.agilebank.model.account.Account
 * @see com.agilebank.model.account.AccountDto
 */
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
