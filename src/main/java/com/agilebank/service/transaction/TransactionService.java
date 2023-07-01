package com.agilebank.service.transaction;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.model.transaction.TransactionDao;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.persistence.TransactionRepository;
import com.agilebank.util.exceptions.*;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionSanityChecker transactionSanityChecker;
  private final CurrencyLedger currencyLedger;

  @Autowired
  public TransactionService(
      AccountRepository accountRepository,
      TransactionRepository transactionRepository,
      TransactionSanityChecker transactionSanityChecker,
      CurrencyLedger currencyLedger) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.transactionSanityChecker = transactionSanityChecker;
    this.currencyLedger = currencyLedger;
  }

  @Transactional // Since there's a bunch of persistence calls
  public TransactionDto storeTransaction(TransactionDto transactionDto)
      throws NonExistentAccountException,
          InvalidAmountException,
          InvalidTransactionCurrencyException,
          SameAccountException,
          InsufficientBalanceException {
    Optional<AccountDao> sourceAccount =
        accountRepository.findById(transactionDto.getSourceAccountId().strip());
    Optional<AccountDao> targetAccount =
        accountRepository.findById(transactionDto.getTargetAccountId().strip());
    Map<CurrencyPair, BigDecimal> currencyExchangeRates = currencyLedger.getCurrencyExchangeRates();
    // Check the transaction for various inconsistencies.
    transactionSanityChecker.checkTransaction(
        transactionDto, sourceAccount, targetAccount, currencyExchangeRates);
    BigDecimal sourceToTransactionCurrencyExchangeRate =
        currencyExchangeRates.get(
            new CurrencyPair(sourceAccount.get().getCurrency(), transactionDto.getCurrency()));
    // Debit the source account in its own currency.
    sourceAccount
        .get()
        .setBalance(
            sourceAccount.get().getBalance().subtract(
                transactionDto.getAmount().multiply(sourceToTransactionCurrencyExchangeRate)));
    // Credit the target account in its own currency.
    targetAccount.get().setBalance(targetAccount.get().getBalance().add(transactionDto.getAmount()));
    // Save the updated accounts.
    accountRepository.save(sourceAccount.get());
    accountRepository.save(targetAccount.get());
    // Finally, save and return the transaction.
    TransactionDao storedTransactionDao = transactionRepository.save(
        new TransactionDao(
            transactionDto.getSourceAccountId().strip(),
            transactionDto.getTargetAccountId().strip(),
            transactionDto.getAmount(),
            transactionDto.getCurrency(),
            new Date()));
    return new TransactionDto(storedTransactionDao.getSourceAccountId(), storedTransactionDao.getTargetAccountId(),
            storedTransactionDao.getAmount().setScale(2, RoundingMode.HALF_EVEN), storedTransactionDao.getCurrency());
  }

  public List<TransactionDto> getAllTransactions() {
    return transactionRepository.findAll().stream()
        .map(
            transactionDao ->
                new TransactionDto(
                    transactionDao.getSourceAccountId(),
                    transactionDao.getTargetAccountId(),
                    transactionDao.getAmount(),
                    transactionDao.getCurrency()))
        .collect(Collectors.toList());
  }

  public List<TransactionDto> getAllTransactionsFrom(String sourceAccountId) {
    return transactionRepository.findBySourceAccountId(sourceAccountId).stream()
        .map(
            transactionDao ->
                new TransactionDto(
                    transactionDao.getSourceAccountId(),
                    transactionDao.getTargetAccountId(),
                    transactionDao.getAmount(),
                    transactionDao.getCurrency()))
        .collect(Collectors.toList());
  }

  public List<TransactionDto> getAllTransactionsTo(String targetAccountId) {
    return transactionRepository.findByTargetAccountId(targetAccountId).stream()
        .map(
            transactionDao ->
                new TransactionDto(
                    transactionDao.getSourceAccountId(),
                    transactionDao.getTargetAccountId(),
                    transactionDao.getAmount(),
                    transactionDao.getCurrency()))
        .collect(Collectors.toList());
  }

  public List<TransactionDto> getAllTransactionsBetween(
      String sourceAccountId, String targetAccountId) {
    return transactionRepository
        .findBySourceAccountIdAndTargetAccountId(sourceAccountId, targetAccountId)
        .stream()
        .map(
            transactionDao ->
                new TransactionDto(
                    transactionDao.getSourceAccountId(),
                    transactionDao.getTargetAccountId(),
                    transactionDao.getAmount(),
                    transactionDao.getCurrency()))
        .collect(Collectors.toList());
  }
}
