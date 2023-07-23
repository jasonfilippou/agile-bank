package com.agilebank.model.transaction;

import com.agilebank.model.Auditable;
import com.agilebank.model.currency.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Transaction database object.
 * 
 * @author jason 
 * 
 * @see TransactionDto
 * @see com.agilebank.model.account.AccountDto
 * @see com.agilebank.model.account.Account
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TRANSACTION")
@EntityListeners(AuditingEntityListener.class)
public class Transaction extends Auditable<String>{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "source_account_id")
  private Long sourceAccountId;

  @Column(name = "target_account_id")
  private Long targetAccountId;

  @Column(name = "amount", scale = 2)
  @Positive
  private BigDecimal amount;

  @Column(name = "currency")
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Transaction that = (Transaction) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}
