package com.agilebank.model.account;

import com.agilebank.model.currency.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ACCOUNT")
public class Account {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "balance", scale = 2)
  private BigDecimal balance;

  @Column(name = "currency")
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Column(name = "created_at")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date createdAt;

  public Account(BigDecimal balance, Currency currency){
    this.balance = balance;
    this.currency = currency;
    createdAt = new Date();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Account account = (Account) o;
    return getId() != null && Objects.equals(getId(), account.getId());
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}
