package com.agilebank.persistence;

import com.agilebank.model.transaction.TransactionDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionDao, Long> {

    List<TransactionDao> findBySourceAccountId(String sourceAccountId);
    List<TransactionDao> findByTargetAccountId(String targetAccountId);
    List<TransactionDao> findBySourceAccountIdAndTargetAccountId(String sourceAccountId, String targetAccountId);
}
