package com.agilebank.persistence;


import com.agilebank.model.account.AccountDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountDao, String> {}
