package com.agilebank.model.account;

import com.agilebank.model.Auditable;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Account database object.
 *
 * @author jason
 * @see Transaction
 * @see AccountDto
 * @see com.agilebank.model.transaction.TransactionDto
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ACCOUNT")
@EntityListeners(AuditingEntityListener.class)
public class Account extends Auditable<String>{

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "balance", scale = 2)
  @Positive
  private BigDecimal balance;

  @Column(name = "currency")
  @Enumerated(EnumType.STRING)
  private Currency currency;
  
  public Account(BigDecimal balance, Currency currency) {
    this.balance = balance;
    this.currency = currency;
  }

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
    Account account = (Account) o;
    return getId() != null && Objects.equals(getId(), account.getId());
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}
