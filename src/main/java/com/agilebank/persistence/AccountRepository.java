package com.agilebank.persistence;

import com.agilebank.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A {@link JpaRepository} for {@link Account} objects.
 * @author jason
 * @see TransactionRepository
 * @see UserRepository
 */
public interface AccountRepository extends JpaRepository<Account, Long> {}
