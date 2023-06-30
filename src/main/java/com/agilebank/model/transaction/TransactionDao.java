package com.agilebank.model.transaction;

import com.agilebank.model.currency.Currency;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "TRANSACTION")
public class TransactionDao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "source_account_id")
  private String sourceAccountId;

  @Column(name = "target_account_id")
  private String targetAccountId;

  @Column(name = "amount", scale = 2)
  private Double amount;

  @Column(name = "currency")
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Column(name = "submitted_at")
  private Date submittedAt;

  public TransactionDao(
      String sourceAccountId,
      String targetAccountId,
      Double amount,
      Currency currency,
      Date submittedAt) {
    this.sourceAccountId = sourceAccountId;
    this.targetAccountId = targetAccountId;
    this.amount = amount;
    this.currency = currency;
    this.submittedAt = submittedAt;
  }
}
