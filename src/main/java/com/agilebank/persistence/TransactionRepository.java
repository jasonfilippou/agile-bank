package com.agilebank.persistence;

import com.agilebank.model.transaction.TransactionDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionDao, Long> {

}
