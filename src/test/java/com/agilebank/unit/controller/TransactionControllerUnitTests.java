package com.agilebank.unit.controller;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.agilebank.controller.TransactionController;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.PaginationTester;
import com.agilebank.unit.controller.mockinglogicfactory.TransactionDependencyMockingFactory;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerUnitTests {

  @InjectMocks private TransactionController transactionController;

  @Mock private TransactionService transactionService;

  @Mock
  private TransactionModelAssembler transactionModelAssembler = new TransactionModelAssembler();

  private static final TransactionDto TEST_TRANSACTION_DTO_ONE = TEST_VALID_TRANSACTION_DTOS.get(0);
  
  private static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE =
      TEST_VALID_TRANSACTION_DTO_ENTITY_MODELS.get(0);
  
  private TransactionDependencyMockingFactory transactionDependencyMockingFactory = new TransactionDependencyMockingFactory(transactionService, transactionModelAssembler);

  @Before
  public void setUp() {
    for(int i = 0; i < TEST_VALID_TRANSACTION_DTOS.size(); i++){
      when(transactionModelAssembler.toModel(TEST_VALID_TRANSACTION_DTOS.get(i)))
              .thenReturn(TEST_VALID_TRANSACTION_DTO_ENTITY_MODELS.get(i));
    }
  }

  /* POST transaction tests */

  @Test
  public void whenPostingANewTransaction_andServiceCompletesSuccessfully_thenReturnTransaction() {
    when(transactionService.storeTransaction(TEST_TRANSACTION_DTO_ONE))
        .thenReturn(TEST_TRANSACTION_DTO_ONE);
    assertEquals(
        new ResponseEntity<>(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, HttpStatus.CREATED),
        transactionController.postTransaction(TEST_TRANSACTION_DTO_ONE));
  }

  /* GET transaction tests */

  @Test
  public void whenGettingATransactionById_thenTransactionIsReturned() {
    when(transactionService.getTransaction(TEST_VALID_TRANSACTION_ENTITIES.get(0).getId()))
            .thenReturn(TEST_VALID_TRANSACTION_DTOS.get(0));
    assertEquals(ResponseEntity.ok(TEST_VALID_TRANSACTION_DTO_ENTITY_MODELS.get(0)), transactionController
            .getTransaction(TEST_VALID_TRANSACTION_ENTITIES.get(0).getId()));
  }

  /* GET from / to / between / all tests */

  @Test
  public void whenGettingAllTransactions_returnAllRelevantTransactions() {
    // There are 64 transactions total.

    // First, test that we return all of the records if we want to, for all sorting fields and for
    // both directions.
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(64)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Now do the same, but for 4 pages of 16 records each.
    PaginationTester.builder()
        .totalPages(4)
        .pageSize(16)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Now, do this for 6 pages of 12 records each (except for the last one, which should have 4
    // since 64 = 5 * 12 + 4).
    PaginationTester.builder()
        .totalPages(6)
        .pageSize(12)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Now 16 pages of 4 records each
    PaginationTester.builder()
        .totalPages(16)
        .pageSize(4)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Finally, 64 pages of 1 record each
    PaginationTester.builder()
        .totalPages(64)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testAggregateGetForGivenParameters);
  }

  private void testAggregateGetForGivenParameters(
      AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Map<String, String> transactionParams = aggregateGetQueryParams.getTransactionQueryParams();
    transactionDependencyMockingFactory
            .mockWithParams(transactionParams.containsKey(SOURCE_ACCOUNT_ID),
            transactionParams.containsKey(TARGET_ACCOUNT_ID), aggregateGetQueryParams);
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> responseEntity =
        transactionController.getAllTransactions(
            transactionParams,
            aggregateGetQueryParams.getPage(),
            aggregateGetQueryParams.getPageSize(),
            aggregateGetQueryParams.getSortByField(),
            aggregateGetQueryParams.getSortOrder());
    Collection<TransactionDto> transactionDtos =
        Objects.requireNonNull(responseEntity.getBody()).getContent().stream()
            .map(EntityModel::getContent)
            .toList();
    assertEquals(transactionDtos.size(), expectedNumberOfRecords);
    assertTrue(
        collectionIsSortedByFieldInGivenDirection(
            transactionDtos,
            aggregateGetQueryParams.getSortByField(),
            aggregateGetQueryParams.getSortOrder()));
  }

  @Test
  public void whenGettingAllTransactionsFromAcc1_returnOnlyThoseTransactions() {
    // There are 4 transactions coming out of the first account.

    // Test a page with all 4
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(4)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Test 2 with 2 each
    PaginationTester.builder()
        .totalPages(2)
        .pageSize(2)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Test 4 with 1 each
    PaginationTester.builder()
        .totalPages(4)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);
  }

  @Test
  public void whenGettingAllTransactionsToAcc1_returnOnlyThoseTransactions() {
    // There are 15 transactions going into account 1.

    // First, test a page with all 15.
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(15)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Now, 5 pages with 3 each.
    PaginationTester.builder()
        .totalPages(5)
        .pageSize(3)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Now, 3 pages with 5 each.

    PaginationTester.builder()
        .totalPages(3)
        .pageSize(5)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);

    // Finally, 15 pages with 1 each.

    PaginationTester.builder()
        .totalPages(15)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);
  }

  @Test
  public void whenGettingAllTransactionsFromAcc1ToAcc2_returnOnlyThoseTransactions() {
    // There is only one transaction going from account 1 to account 2.
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(
            Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L), TARGET_ACCOUNT_ID, Long.toString(2L)))
        .build()
        .runTest(this::testAggregateGetForGivenParameters);
  }

  @Test
  public void whenDeletingATransactionSucceeds_thenReturnNoContent() {
    doNothing().when(transactionService).deleteTransaction(TEST_TRANSACTION_DTO_ONE.getId());
    assertEquals(
        ResponseEntity.noContent().build(),
        transactionController.deleteTransaction(TEST_TRANSACTION_DTO_ONE.getId()));
  }

  @Test
  public void whenDeletingAllTransactions_thenReturnNoContent() {
    doNothing().when(transactionService).deleteAllTransactions();
    assertEquals(ResponseEntity.noContent().build(), transactionController.deleteAllTransactions());
  }
}
