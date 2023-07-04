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
  public TransactionDto storeTransaction(TransactionDto transactionDto) {
    Optional<Account> sourceAccount =
        accountRepository.findById(transactionDto.getSourceAccountId());
    Optional<Account> targetAccount =
        accountRepository.findById(transactionDto.getTargetAccountId());
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
            Transaction.builder()
                .sourceAccountId(transactionDto.getSourceAccountId())
                .targetAccountId(transactionDto.getTargetAccountId())
                .amount(transactionDto.getAmount())
                .currency(transactionDto.getCurrency())
                .build());
    return TransactionDto.builder()
        .id(storedTransaction.getId())
        .sourceAccountId(storedTransaction.getSourceAccountId())
        .targetAccountId(storedTransaction.getTargetAccountId())
        .currency(storedTransaction.getCurrency())
        .amount(storedTransaction.getAmount())
        .build();
  }

  @Transactional(readOnly = true)
  public TransactionDto getTransaction(Long id) throws TransactionNotFoundException {
    return transactionRepository
        .findById(id)
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .orElseThrow(() -> new TransactionNotFoundException(id));
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactions() {
    return transactionRepository.findAll().stream()
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsFrom(Long sourceAccountId) {
    return transactionRepository.findBySourceAccountId(sourceAccountId).stream()
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsTo(Long targetAccountId) {
    return transactionRepository.findByTargetAccountId(targetAccountId).stream()
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsBetween(
      Long sourceAccountId, Long targetAccountId) {
    return transactionRepository
        .findBySourceAccountIdAndTargetAccountId(sourceAccountId, targetAccountId)
        .stream()
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .collect(Collectors.toList());
  }

  @Transactional
  public void deleteTransaction(Long id) {
    Optional<Transaction> transaction = transactionRepository.findById(id);
    if (transaction.isPresent()) {
      transactionRepository.deleteById(id);
    } else {
      throw new TransactionNotFoundException(id);
    }
  }

  @Transactional
  public void deleteAllTransactions() {
    transactionRepository.deleteAll();
  }
}
