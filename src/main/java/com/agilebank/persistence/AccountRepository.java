package com.agilebank.persistence;

import com.agilebank.model.account.Account;
import com.agilebank.util.Logged;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

/**
 * A {@link JpaRepository} for {@link Account} objects.
 * @author jason
 * @see TransactionRepository
 * @see UserRepository
 */
@Logged
public interface AccountRepository extends JpaRepository<Account, Long>{

    @Modifying
    @Query("update Account a set a.balance = :balance where a.id = :id")
    void updateBalance(@Param(value = "id") long id, @Param(value = "balance") BigDecimal balance);
}
