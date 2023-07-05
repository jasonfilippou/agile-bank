package com.agilebank.persistence;

import com.agilebank.model.transaction.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A {@link JpaRepository} for {@link Transaction} objects.
 * @author jason
 * @see AccountRepository
 * @see UserRepository
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findBySourceAccountId(Long sourceAccountId);

  List<Transaction> findByTargetAccountId(Long targetAccountId);

  List<Transaction> findBySourceAccountIdAndTargetAccountId(
      Long sourceAccountId, Long targetAccountId);
}
