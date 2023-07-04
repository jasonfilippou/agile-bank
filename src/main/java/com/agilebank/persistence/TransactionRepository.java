package com.agilebank.persistence;

import com.agilebank.model.transaction.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findBySourceAccountId(Long sourceAccountId);

  List<Transaction> findByTargetAccountId(Long targetAccountId);

  List<Transaction> findBySourceAccountIdAndTargetAccountId(
      Long sourceAccountId, Long targetAccountId);
}
