package com.agilebank.model.transaction;

import com.agilebank.model.currency.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
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
  @Schema(example = "1", hidden = true)
  private Long id;
  @Schema(example = "1")
  @NonNull private Long sourceAccountId;
  @Schema(example = "2")
  @NonNull private Long targetAccountId;
  @Schema(example = "10")
  @Positive
  @NonNull private BigDecimal amount;
  @Schema(example = "USD")
  @NonNull private Currency currency;
}
