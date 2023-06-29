package com.agilebank.model.transaction;

import com.agilebank.model.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;


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

    @Column(name = "amount")
    private Long amount;

    @Column(name = "currency")
    private Currency currency;

    @Column(name = "submitted_at")
    private Date submittedAt;

    public TransactionDao(String sourceAccountId, String targetAccountId, Long amount, Currency currency, Date submittedAt){
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
        this.submittedAt = submittedAt;
    }
}
