package com.agilebank.service.transaction;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.account.Account;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.model.transaction.Transaction;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.persistence.TransactionRepository;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionSanityChecker transactionSanityChecker;
  private final CurrencyLedger currencyLedger;

  @Transactional
  public TransactionDto storeTransaction(TransactionDto transactionDto)
      throws NonExistentAccountException,
          InvalidAmountException,
          InvalidTransactionCurrencyException,
          SameAccountException,
          InsufficientBalanceException {
    Optional<Account> sourceAccount =
        accountRepository.findById(transactionDto.getSourceAccountId().strip());
    Optional<Account> targetAccount =
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
            sourceAccount
                .get()
                .getBalance()
                .subtract(
                    transactionDto.getAmount().multiply(sourceToTransactionCurrencyExchangeRate)));
    // Credit the target account in its own currency.
    targetAccount
        .get()
        .setBalance(targetAccount.get().getBalance().add(transactionDto.getAmount()));
    // Save the updated accounts.
    accountRepository.save(sourceAccount.get());
    accountRepository.save(targetAccount.get());
    // Finally, save and return the transaction.
    Transaction storedTransaction =
        transactionRepository.save(
            new Transaction(
                transactionDto.getSourceAccountId().strip(),
                transactionDto.getTargetAccountId().strip(),
                transactionDto.getAmount(),
                transactionDto.getCurrency(),
                new Date()));
    return new TransactionDto(
        storedTransaction.getId(),
        storedTransaction.getSourceAccountId(),
        storedTransaction.getTargetAccountId(),
        storedTransaction.getAmount().setScale(2, RoundingMode.HALF_EVEN),
        storedTransaction.getCurrency());
  }

  @Transactional(readOnly = true)
  public TransactionDto getTransaction(Long id) throws TransactionNotFoundException{
    return transactionRepository
        .findById(id)
        .map(
            transaction ->
                new TransactionDto(
                    transaction.getId(),
                    transaction.getSourceAccountId(),
                    transaction.getTargetAccountId(),
                    transaction.getAmount(),
                    transaction.getCurrency()))
        .orElseThrow(() -> new TransactionNotFoundException(id));
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactions() {
    return transactionRepository.findAll().stream()
        .map(
            transaction ->
                new TransactionDto(
                        transaction.getId(),
                    transaction.getSourceAccountId(),
                    transaction.getTargetAccountId(),
                    transaction.getAmount(),
                    transaction.getCurrency()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsFrom(String sourceAccountId) {
    return transactionRepository.findBySourceAccountId(sourceAccountId).stream()
        .map(
            transaction ->
                new TransactionDto(
                        transaction.getId(),
                    transaction.getSourceAccountId(),
                    transaction.getTargetAccountId(),
                    transaction.getAmount(),
                    transaction.getCurrency()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsTo(String targetAccountId) {
    return transactionRepository.findByTargetAccountId(targetAccountId).stream()
        .map(
            transaction ->
                new TransactionDto(
                        transaction.getId(),
                    transaction.getSourceAccountId(),
                    transaction.getTargetAccountId(),
                    transaction.getAmount(),
                    transaction.getCurrency()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsBetween(
      String sourceAccountId, String targetAccountId) {
    return transactionRepository
        .findBySourceAccountIdAndTargetAccountId(sourceAccountId, targetAccountId)
        .stream()
        .map(
            transaction ->
                new TransactionDto(
                        transaction.getId(),
                    transaction.getSourceAccountId(),
                    transaction.getTargetAccountId(),
                    transaction.getAmount(),
                    transaction.getCurrency()))
        .collect(Collectors.toList());
  }
}
