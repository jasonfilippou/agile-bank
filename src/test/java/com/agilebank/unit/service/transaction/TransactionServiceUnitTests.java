package com.agilebank.unit.service.transaction;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.agilebank.model.account.Account;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.model.transaction.Transaction;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.persistence.TransactionRepository;
import com.agilebank.service.transaction.TransactionSanityChecker;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.PaginationTester;
import com.agilebank.util.SortOrder;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceUnitTests {

  @InjectMocks private TransactionService transactionService;
  @Mock private AccountRepository accountRepository;
  @Mock private TransactionRepository transactionRepository;
  @Mock private TransactionSanityChecker transactionSanityChecker;
  @Mock private CurrencyLedger currencyLedger;

  @Before
  public void setUp() {
    when(currencyLedger.getCurrencyExchangeRates()).thenReturn(TEST_EXCHANGE_RATES);
  }

  /* Storing transaction tests */

  @Test
  public void whenStoringATransactionSuccessfully_thenTransactionIsReturned() {
    TransactionDto transactionDto = TEST_VALID_TRANSACTION_DTOS.get(0);
    Transaction transactionEntity = TEST_VALID_TRANSACTION_ENTITIES.get(0);
    Optional<Account> sourceAccountEntity = TEST_ACCOUNT_ENTITIES.stream()
            .filter(account -> account.getId().equals(transactionDto.getSourceAccountId()))
                    .findFirst();
    Optional<Account> targetAccountEntity = TEST_ACCOUNT_ENTITIES.stream()
            .filter(account -> account.getId().equals(transactionDto.getTargetAccountId()))
            .findFirst();
    when(accountRepository.findById(transactionDto.getSourceAccountId()))
        .thenReturn(sourceAccountEntity);
    when(accountRepository.findById(transactionDto.getTargetAccountId()))
        .thenReturn(targetAccountEntity);
    // We don't really care about the balances checking out for this test,
    // the transactionSanityChecker can do nothing.
    doNothing()
        .when(transactionSanityChecker)
        .checkTransaction(
            transactionDto,
            sourceAccountEntity,
            targetAccountEntity,
            TEST_EXCHANGE_RATES);
    doNothing().when(accountRepository).updateBalance(any(Long.class), any(BigDecimal.class));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionEntity);
    assertEquals(TransactionDto.builder()
            .id(TEST_VALID_TRANSACTION_ENTITIES.get(0).getId())
            .currency(transactionDto.getCurrency())
            .amount(transactionDto.getAmount())
            .sourceAccountId(transactionDto.getSourceAccountId())
            .targetAccountId(transactionDto.getTargetAccountId())
            .build(), transactionService.storeTransaction(transactionDto));
  }
  
  /* Getting transaction tests */

  @Test
  public void whenGettingAStoredTransactionById_thenTransactionIsReturned() {
    TransactionDto transactionDto = TEST_VALID_TRANSACTION_DTOS.get(0);
    Transaction transactionEntity = TEST_VALID_TRANSACTION_ENTITIES.get(0);
    when(transactionRepository.findById(transactionEntity.getId()))
        .thenReturn(Optional.of(transactionEntity));
    assertEquals(
            TransactionDto.builder()
                    .id(TEST_VALID_TRANSACTION_ENTITIES.get(0).getId())
                    .currency(transactionDto.getCurrency())
                    .amount(transactionDto.getAmount())
                    .sourceAccountId(transactionDto.getSourceAccountId())
                    .targetAccountId(transactionDto.getTargetAccountId())
                    .build(),
        transactionService.getTransaction(transactionEntity.getId()));
  }

  @Test(expected = TransactionNotFoundException.class)
  public void whenGettingAMissingTransaction_thenATransactionNotFoundExceptionIsThrown() {
    Transaction transactionEntity = TEST_VALID_TRANSACTION_ENTITIES.get(0);
    when(transactionRepository.findById(transactionEntity.getId()))
        .thenReturn(Optional.empty());
    transactionService.getTransaction(transactionEntity.getId());
  }

  /* Getting all / from / to / between tests */
  @Test
  public void whenGettingAllTransactions_AllTransactionsAreReturned() {
    // There are 64 transactions total.

    // First, test that we return all of the records if we want to
    PaginationTester.builder()
            .totalPages(1)
            .pageSize(64)
            .pojoType(TransactionDto.class)
            .accountParams(Collections.emptyMap())
            .build()
            .runTest(this::testAggregateGetAllForGivenParameters);

    // Now do the same, but for 4 pages of 16 records each.
    PaginationTester.builder()
            .totalPages(4)
            .pageSize(16)
            .pojoType(TransactionDto.class)
            .accountParams(Collections.emptyMap())
            .build()
            .runTest(this::testAggregateGetAllForGivenParameters);

    // Now, do this for 6 pages of 12 records each (except for the last one, which should have 4
    // since 64 = 5 * 12 + 4).
    PaginationTester.builder()
            .totalPages(6)
            .pageSize(12)
            .pojoType(TransactionDto.class)
            .accountParams(Collections.emptyMap())
            .build()
            .runTest(this::testAggregateGetAllForGivenParameters);

    // Now 16 pages of 4 records each
    PaginationTester.builder()
            .totalPages(16)
            .pageSize(4)
            .pojoType(TransactionDto.class)
            .accountParams(Collections.emptyMap())
            .build()
            .runTest(this::testAggregateGetAllForGivenParameters);

    // Finally, 64 pages of 1 record each
    PaginationTester.builder()
            .totalPages(64)
            .pageSize(1)
            .pojoType(TransactionDto.class)
            .accountParams(Collections.emptyMap())
            .build()
            .runTest(this::testAggregateGetAllForGivenParameters);
  }

  private void testAggregateGetAllForGivenParameters(
          AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    List<Transaction> subListOfPage =
            TEST_VALID_TRANSACTION_ENTITIES.stream()
                    .sorted(
                            (t1, t2) ->
                                    compareFieldsInGivenOrder(t1.getClass(), t2.getClass(), sortByField, sortOrder))
                    .toList()
                    .subList(page * pageSize, pageSize * (page + 1));
    when(transactionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(subListOfPage));
    Collection<TransactionDto> transactionDtos =
            transactionService.getAllTransactions(page, pageSize, sortByField, sortOrder)
                    .stream().toList();
    assertEquals(transactionDtos.size(), expectedNumberOfRecords);
    assertTrue(collectionIsSortedByFieldInGivenDirection(transactionDtos, sortByField, sortOrder));
  }

  @Test
  public void whenGettingAllTransactionsFromASourceAccount_thenOnlyThoseAreReturned() {
    // There are 4 transactions coming out of the first account.

    // Test a page with all 4
    PaginationTester.builder()
            .totalPages(1)
            .pageSize(4)
            .pojoType(TransactionDto.class)
            .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
            .build()
            .runTest(this::testAggregateGetAllFromSourceAccountForGivenParameters);

    // Test 2 with 2 each
    PaginationTester.builder()
            .totalPages(2)
            .pageSize(2)
            .pojoType(TransactionDto.class)
            .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
            .build()
            .runTest(this::testAggregateGetAllFromSourceAccountForGivenParameters);

    // Test 4 with 1 each
    PaginationTester.builder()
            .totalPages(4)
            .pageSize(1)
            .pojoType(TransactionDto.class)
            .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
            .build()
            .runTest(this::testAggregateGetAllFromSourceAccountForGivenParameters);
  }

  private void testAggregateGetAllFromSourceAccountForGivenParameters(
          AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    Map<String, String> transactionQueryParams = aggregateGetQueryParams.getTransactionQueryParams();
    Long sourceAccountId = Long.valueOf(transactionQueryParams.get(SOURCE_ACCOUNT_ID));
    List<Transaction> subListOfPage =
            TEST_VALID_TRANSACTION_ENTITIES.stream()
                    .filter(transaction -> transaction.getSourceAccountId().equals(sourceAccountId))
                    .sorted(
                            (t1, t2) ->
                                    compareFieldsInGivenOrder(t1.getClass(), t2.getClass(), sortByField, sortOrder))
                    .toList()
                    .subList(page * pageSize, pageSize * (page + 1));
    when(transactionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(subListOfPage));
    Collection<TransactionDto> transactionDtos =
            transactionService.getAllTransactionsFrom(sourceAccountId, page, pageSize, sortByField, sortOrder)
                    .stream().toList();
    assertEquals(transactionDtos.size(), expectedNumberOfRecords);
    assertTrue(collectionIsSortedByFieldInGivenDirection(transactionDtos, sortByField, sortOrder));
  }

  @Test
  public void whenGettingAllTransactionsToATargetAccount_thenOnlyThoseAreReturned() {
    // There are 15 transactions going into account 1.

    // First, test a page with all 15.
    PaginationTester.builder()
            .totalPages(1)
            .pageSize(15)
            .pojoType(TransactionDto.class)
            .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
            .build()
            .runTest(this::testAggregateGetAllToTargetAccountForGivenParameters);

    // Now, 5 pages with 3 each.
    PaginationTester.builder()
            .totalPages(5)
            .pageSize(3)
            .pojoType(TransactionDto.class)
            .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
            .build()
            .runTest(this::testAggregateGetAllToTargetAccountForGivenParameters);

    // Now, 3 pages with 5 each.

    PaginationTester.builder()
            .totalPages(3)
            .pageSize(5)
            .pojoType(TransactionDto.class)
            .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
            .build()
            .runTest(this::testAggregateGetAllToTargetAccountForGivenParameters);
  }

  private void testAggregateGetAllToTargetAccountForGivenParameters(
          AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    Map<String, String> transactionQueryParams = aggregateGetQueryParams.getTransactionQueryParams();
    Long targetAccountId = Long.valueOf(transactionQueryParams.get(TARGET_ACCOUNT_ID));
    List<Transaction> subListOfPage =
            TEST_VALID_TRANSACTION_ENTITIES.stream()
                    .filter(transaction -> transaction.getTargetAccountId().equals(targetAccountId))
                    .sorted(
                            (t1, t2) ->
                                    compareFieldsInGivenOrder(t1.getClass(), t2.getClass(), sortByField, sortOrder))
                    .toList()
                    .subList(page * pageSize, pageSize * (page + 1));
    when(transactionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(subListOfPage));
    Collection<TransactionDto> transactionDtos =
            transactionService.getAllTransactionsTo(targetAccountId, page, pageSize, sortByField, sortOrder)
                    .stream().toList();
    assertEquals(transactionDtos.size(), expectedNumberOfRecords);
    assertTrue(collectionIsSortedByFieldInGivenDirection(transactionDtos, sortByField, sortOrder));
  }

  @Test
  public void
      whenGettingAllTransactionsBetweenAGivenSourceAndAGivenTargetAccount_thenOnlyThoseAreReturned() {
    // There is only one transaction going from account 1 to account 2.
    PaginationTester.builder()
            .totalPages(1)
            .pageSize(1)
            .pojoType(TransactionDto.class)
            .accountParams(
                    Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L), TARGET_ACCOUNT_ID, Long.toString(2L)))
            .build()
            .runTest(this::testAggregateGetBetweenSourceAndTargetAccountsForGivenParameters);
  }

  private void testAggregateGetBetweenSourceAndTargetAccountsForGivenParameters(
          AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    Map<String, String> transactionQueryParams = aggregateGetQueryParams.getTransactionQueryParams();
    Long sourceAccountId = Long.valueOf(transactionQueryParams.get(SOURCE_ACCOUNT_ID));
    Long targetAccountId = Long.valueOf(transactionQueryParams.get(TARGET_ACCOUNT_ID));
    List<Transaction> subListOfPage =
            TEST_VALID_TRANSACTION_ENTITIES.stream()
                    .filter(transaction -> transaction.getSourceAccountId().equals(sourceAccountId) && transaction.getTargetAccountId().equals(targetAccountId))
                    .sorted(
                            (t1, t2) ->
                                    compareFieldsInGivenOrder(t1.getClass(), t2.getClass(), sortByField, sortOrder))
                    .toList()
                    .subList(page * pageSize, pageSize * (page + 1));
    when(transactionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(subListOfPage));
    Collection<TransactionDto> transactionDtos =
            transactionService.getAllTransactionsTo(targetAccountId, page, pageSize, sortByField, sortOrder)
                    .stream().toList();
    assertEquals(transactionDtos.size(), expectedNumberOfRecords);
    assertTrue(collectionIsSortedByFieldInGivenDirection(transactionDtos, sortByField, sortOrder));
  }
  /* Delete by ID tests */

  @Test
  public void whenDeletingATransactionThatExistsInRepo_thenOk() {
    Transaction transactionEntity = TEST_VALID_TRANSACTION_ENTITIES.get(0);
    when(transactionRepository.findById(transactionEntity.getId()))
        .thenReturn(Optional.of(transactionEntity));
    Throwable expected = null;
    doNothing().when(transactionRepository).deleteById(transactionEntity.getId());
    try {
      transactionService.deleteTransaction(transactionEntity.getId());
    } catch (Throwable thrown) {
      expected = thrown;
    }
    assertNull(expected, "Expected nothing to be thrown by service method.");
  }

  @Test(expected = TransactionNotFoundException.class)
  public void
      whenDeletingATransactionThatDoesNotExistInRepo_ThenTransactionNotFoundExceptionIsThrown() {
    Transaction transactionEntity = TEST_VALID_TRANSACTION_ENTITIES.get(0);
    when(transactionRepository.findById(transactionEntity.getId()))
        .thenReturn(Optional.empty());
    transactionService.deleteTransaction(transactionEntity.getId());
  }

  /* Delete ALL tests */

  @Test
  public void whenDeletingAllTransactions_thenOk() {
    doNothing().when(transactionRepository).deleteAll();
    Throwable expected = null;
    try {
      transactionService.deleteAllTransactions();
    } catch (Throwable thrown) {
      expected = thrown;
    }
    assertNull(expected, "Expected nothing to be thrown by service method.");
  }
}
