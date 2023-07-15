package com.agilebank.service.transaction;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.account.Account;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.model.transaction.Transaction;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.persistence.TransactionRepository;
import com.agilebank.util.SortOrder;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for transactions. Supports {@link com.agilebank.controller.TransactionController} by providing methods for
 * retrieving a single or multiple transactions, storing new transactions, and deleting transactions.
 * 
 * @author jason 
 * 
 * @see com.agilebank.controller.TransactionController
 * @see TransactionRepository
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionSanityChecker transactionSanityChecker;
  private final CurrencyLedger currencyLedger;

  /**
   * Store a new transaction in the database.
   * @param transactionDto The {@link TransactionDto} with the information of the new transaction to persist.
   * @return A {@link TransactionDto} with the information of the just persisted transaction in the database (if everything goes ok).
   * @see TransactionSanityChecker
   */
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
    accountRepository.updateBalance(sourceAccount.get().getId(), sourceAccount.get().getBalance());
    accountRepository.updateBalance(targetAccount.get().getId(), targetAccount.get().getBalance());
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

  /**
   * Retrieve a single transaction by its unique ID.
   * @param id The unique DB id of a transaction.
   * @return A {@link TransactionDto} describing the transaction identified by {@literal id}.
   * @throws TransactionNotFoundException If there is no transaction identified by {@literal id} in the database.
   */
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

  /**
   * Retrieve all the transactions in the database.
   * @return A {@link List} with all the transactions that are stored in the database.
   */
  @Transactional(readOnly = true)
  public Page<TransactionDto> getAllTransactions(Integer page, Integer pageSize,
                                                 String sortByField, SortOrder sortOrder) {
    Sort sorter = (sortOrder == SortOrder.ASC) ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
    List<TransactionDto> transactions = transactionRepository.findAll(PageRequest.of(page, pageSize, sorter)).stream()
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .toList();
    return new PageImpl<>(transactions);
  }

  /**
   * Get all the transactions beginning from a given source account.
   * @param sourceAccountId The unique DB Id of the source account for all the transactions we want.
   * @return A List of all transactions with the source account corresponding to {@literal sourceAccountId}.
   */
  @Transactional(readOnly = true)
  public Page<TransactionDto> getAllTransactionsFrom(Long sourceAccountId, Integer page, Integer pageSize,
                                                     String sortByField, SortOrder sortOrder) {
    Sort sorter = (sortOrder == SortOrder.ASC) ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
    List<TransactionDto> transactions = transactionRepository.findBySourceAccountId(sourceAccountId, 
                    PageRequest.of(page, pageSize, sorter)).stream()
        .map(
            transaction ->
                TransactionDto.builder()
                    .id(transaction.getId())
                    .sourceAccountId(transaction.getSourceAccountId())
                    .targetAccountId(transaction.getTargetAccountId())
                    .currency(transaction.getCurrency())
                    .amount(transaction.getAmount())
                    .build())
        .toList();
    return new PageImpl<>(transactions);
  }

  /**
   * Get all the transactions to a given target account.
   * @param targetAccountId The unique DB Id of the target account for all the transactions we want.
   * @return A List of all transactions with the target account corresponding to {@literal targetAccountId}.
   */
  @Transactional(readOnly = true)
  public List<TransactionDto> getAllTransactionsTo(Long targetAccountId, Integer page, Integer pageSize,
                                                   String sortByField, SortOrder sortOrder) {
    Sort sorter = (sortOrder == SortOrder.ASC) ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
    return transactionRepository.findByTargetAccountId(targetAccountId, PageRequest.of(page, pageSize, sorter)).stream()
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

  /**
   * Get all the transactions beginning from a given source account and to a given target account.
   * @param sourceAccountId The unique DB id of the source account for all the transactions we want.
   * @param targetAccountId The unique DB Id of the target account for all the transactions we want.
   * @return A List of all transactions with the source and target accounts correponding to the IDs provided.
   */
  @Transactional(readOnly = true)
  public Page<TransactionDto> getAllTransactionsBetween(
      Long sourceAccountId, Long targetAccountId, Integer page, Integer pageSize,
      String sortByField, SortOrder sortOrder) {
    Sort sorter = (sortOrder == SortOrder.ASC) ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
    List<TransactionDto> transactions = transactionRepository
        .findBySourceAccountIdAndTargetAccountId(sourceAccountId, targetAccountId, PageRequest.of(page, pageSize, sorter))
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
        .toList();
    return new PageImpl<>(transactions);
  }

  /**
   * Hard-Delete the transaction corresponding to the provided id.
   * @param id The unique DB id of the transaction to delete.
   * @throws TransactionNotFoundException If there is no transaction with id {@literal id} in the DB.
   */
  @Transactional
  public void deleteTransaction(Long id) throws TransactionNotFoundException{
    Optional<Transaction> transaction = transactionRepository.findById(id);
    if (transaction.isPresent()) {
      transactionRepository.deleteById(id);
    } else {
      throw new TransactionNotFoundException(id);
    }
  }

  /**
   * Hard-delete all the transactions in the database, emptying the relevant table.
   */
  @Transactional
  public void deleteAllTransactions() {
    transactionRepository.deleteAll();
  }
}
