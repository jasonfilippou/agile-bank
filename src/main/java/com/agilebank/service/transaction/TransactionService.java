package com.agilebank.service.transaction;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.transaction.TransactionDao;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.persistence.TransactionRepository;
import com.agilebank.util.exceptions.InsufficientBalanceException;
import com.agilebank.util.exceptions.InvalidAmountException;
import com.agilebank.util.exceptions.NonExistentAccountException;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionSanityChecker transactionSanityChecker;

  @Autowired
  public TransactionService(
      AccountRepository accountRepository,
      TransactionRepository transactionRepository,
      TransactionSanityChecker transactionSanityChecker) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.transactionSanityChecker = transactionSanityChecker;
  }

  @Transactional
  public void storeTransaction(TransactionDto transactionDto)
      throws NonExistentAccountException, InvalidAmountException, InsufficientBalanceException {
    Optional<AccountDao> sourceAccount =
        accountRepository.findById(transactionDto.getSourceAccountId().strip());
    Optional<AccountDao> targetAccount =
        accountRepository.findById(transactionDto.getTargetAccountId().strip());
    transactionSanityChecker.checkTransaction(transactionDto, sourceAccount, targetAccount);
    sourceAccount.get().setBalance(sourceAccount.get().getBalance() - transactionDto.getAmount());
    targetAccount.get().setBalance(targetAccount.get().getBalance() + transactionDto.getAmount());
    accountRepository.save(sourceAccount.get());
    accountRepository.save(targetAccount.get());
    transactionRepository.save(
        new TransactionDao(
            transactionDto.getSourceAccountId(),
            transactionDto.getTargetAccountId(),
            transactionDto.getAmount(),
            transactionDto.getCurrency(),
            new Date()));
  }
}
