package com.agilebank.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "`USER`") // Need backticks because "USER" is a reserved table in H2 and tests are affected.
public class UserDao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username", unique = true)
  private String username;

  @Column(name = "password")
  @JsonIgnore
  @ToString.Exclude
  private String password;

  public UserDao(String username, String password) {
    this.username = username;
    this.password = password;
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
    UserDao userDao = (UserDao) o;
    return getId() != null && Objects.equals(getId(), userDao.getId());
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode();
  }
}
