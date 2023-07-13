package com.agilebank.model.account;

import com.agilebank.model.currency.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

/**
 * Data Transfer POJO for accounts.
 *
 * @author jason
 * @see Account
 * @see com.agilebank.model.transaction.TransactionDto
 * @see com.agilebank.model.transaction.Transaction
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountDto {
  @Schema(example = "1", hidden = true)
  private Long id;
  @Schema(example = "100")
  private BigDecimal balance;
  @Schema(example = "USD")
  private Currency currency;
}
