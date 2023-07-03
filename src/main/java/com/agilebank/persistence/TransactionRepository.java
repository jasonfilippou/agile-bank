package com.agilebank.persistence;

import com.agilebank.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountId(Long sourceAccountId);
    List<Transaction> findByTargetAccountId(Long targetAccountId);
    List<Transaction> findBySourceAccountIdAndTargetAccountId(Long sourceAccountId, Long targetAccountId);
}
