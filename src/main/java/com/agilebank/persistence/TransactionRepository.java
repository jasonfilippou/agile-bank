package com.agilebank.persistence;

import com.agilebank.model.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A {@link JpaRepository} for {@link Transaction} objects.
 * @author jason
 * @see AccountRepository
 * @see UserRepository
 */

@CustomRepositoryAnnotation
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  Page<Transaction> findBySourceAccountId(Long sourceAccountId, Pageable pageable);

  Page<Transaction> findByTargetAccountId(Long targetAccountId, Pageable pageable);

  Page<Transaction> findBySourceAccountIdAndTargetAccountId(
      Long sourceAccountId, Long targetAccountId, Pageable pageable);
}
