package com.agilebank.persistence;

import com.agilebank.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountId(String sourceAccountId);
    List<Transaction> findByTargetAccountId(String targetAccountId);
    List<Transaction> findBySourceAccountIdAndTargetAccountId(String sourceAccountId, String targetAccountId);
}
