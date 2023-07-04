package com.agilebank.persistence;

import com.agilebank.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> updateAccountById(Long id);
}
