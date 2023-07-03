package com.agilebank.unit.controller;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.agilebank.controller.TransactionController;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.exceptions.InsufficientBalanceException;
import com.agilebank.util.exceptions.InvalidAmountException;
import com.agilebank.util.exceptions.NonExistentAccountException;
import com.agilebank.util.exceptions.TransactionNotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerUnitTests {

  @InjectMocks private TransactionController transactionController;

  @Mock private TransactionService transactionService;

  @Mock
  private TransactionModelAssembler transactionModelAssembler = new TransactionModelAssembler();

  @Before
  public void setUp() {
    when(transactionModelAssembler.toModel(TEST_TRANSACTION_DTO_ONE))
        .thenReturn(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE);
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

  @Test(expected = NonExistentAccountException.class)
  public void
      whenPostingANewTransaction_andServiceThrowsNonExistentAccountException_thenExceptionBubblesUp() {
    doThrow(new NonExistentAccountException(TEST_TRANSACTION_DTO_ONE.getSourceAccountId()))
        .when(transactionService)
        .storeTransaction(TEST_TRANSACTION_DTO_ONE);
    transactionController.postTransaction(TEST_TRANSACTION_DTO_ONE);
  }

  @Test(expected = InvalidAmountException.class)
  public void
      whenPostingANewTransaction_andServiceThrowsInvalidAmountException_thenExceptionBubblesUp() {
    doThrow(new InvalidAmountException(BigDecimal.TEN))
        .when(transactionService)
        .storeTransaction(TEST_TRANSACTION_DTO_ONE);
    transactionController.postTransaction(TEST_TRANSACTION_DTO_ONE);
  }

  @Test(expected = InsufficientBalanceException.class)
  public void
      whenPostingANewTransaction_andServicethrowsInsufficientBalanceException_thenExceptionBubblesUp() {
    doThrow(
            new InsufficientBalanceException(
                TEST_TRANSACTION_DTO_ONE.getSourceAccountId(), BigDecimal.ZERO, Currency.GBP, BigDecimal.ONE))
        .when(transactionService)
        .storeTransaction(TEST_TRANSACTION_DTO_ONE);
    transactionController.postTransaction(TEST_TRANSACTION_DTO_ONE);
  }

  /* GET transaction tests */
  
  @Test
  public void whenPostingANewTransaction_thenGettingItByIdReturnsTheTransaction(){
    when(transactionService.getTransaction(TEST_TRANSACTION_DTO_ONE.getId())).thenReturn(TEST_TRANSACTION_DTO_ONE);
    when(transactionService.getTransaction(TEST_TRANSACTION_DTO_TWO.getId())).thenReturn(TEST_TRANSACTION_DTO_TWO);
    when(transactionService.getTransaction(TEST_TRANSACTION_DTO_THREE.getId())).thenReturn(TEST_TRANSACTION_DTO_THREE);
    when(transactionService.getTransaction(TEST_TRANSACTION_DTO_FOUR.getId())).thenReturn(TEST_TRANSACTION_DTO_FOUR);
    assertEquals(TEST_TRANSACTION_DTO_ONE, transactionService.getTransaction(TEST_TRANSACTION_DTO_ONE.getId()));
    assertEquals(TEST_TRANSACTION_DTO_TWO, transactionService.getTransaction(TEST_TRANSACTION_DTO_TWO.getId()));
    assertEquals(TEST_TRANSACTION_DTO_THREE, transactionService.getTransaction(TEST_TRANSACTION_DTO_THREE.getId()));
    assertEquals(TEST_TRANSACTION_DTO_FOUR, transactionService.getTransaction(TEST_TRANSACTION_DTO_FOUR.getId()));
  }
  
  @Test(expected = TransactionNotFoundException.class)
  public void whenGettingATransaction_andServiceThrowsTransactionNotFoundException_thenExceptionBubblesUp(){
    doThrow(new TransactionNotFoundException(1L)).when(transactionService).getTransaction(1L);
    transactionController.getTransaction(1L);
  }
  
  /* GET from / to / between / all tests */

  @Test
  public void whenGettingAllTransactions_returnAllTransactions() {
    when(transactionService.getAllTransactions())
        .thenReturn(
            List.of(
                TEST_TRANSACTION_DTO_ONE,
                TEST_TRANSACTION_DTO_TWO,
                TEST_TRANSACTION_DTO_THREE,
                TEST_TRANSACTION_DTO_FOUR));
    when(transactionModelAssembler.toCollectionModel(
      List.of(
              TEST_TRANSACTION_DTO_ONE,
              TEST_TRANSACTION_DTO_TWO,
              TEST_TRANSACTION_DTO_THREE,
              TEST_TRANSACTION_DTO_FOUR), Collections.emptyMap()))
            .thenReturn(TEST_ENTITY_MODEL_COLLECTION_MODEL_FULL);
    assertEquals(
        ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_FULL),
        transactionController.getAllTransactions(Collections.emptyMap()));
  }

  @Test
  public void whenGettingAllTransactionsFromAcc1_returnOnlyThoseTransactions() {
    when(transactionService.getAllTransactionsFrom(TEST_ACCOUNT_ONE_ID))
        .thenReturn(
            List.of(
                TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE)); // Those three transactions are from Acc1.
    when(transactionModelAssembler.toCollectionModel(
            List.of(
                    TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE),
            Map.of(SOURCE_ACCOUNT_ID, TEST_ACCOUNT_DTO_ONE.getId())))
            .thenReturn(TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE);
    assertEquals(
        ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE),
        transactionController.getAllTransactions(Map.of(SOURCE_ACCOUNT_ID, TEST_ACCOUNT_ONE_ID)));
  }

  @Test
  public void whenGettingAllTransactionsToAcc2_returnOnlyThoseTransactions() {
    when(transactionService.getAllTransactionsTo(TEST_ACCOUNT_TWO_ID))
        .thenReturn(
            List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_FOUR)); // Those are all to Acc2
    when(transactionModelAssembler.toCollectionModel(
            List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_FOUR),
            Map.of(TARGET_ACCOUNT_ID, TEST_ACCOUNT_DTO_TWO.getId())))
            .thenReturn(TEST_ENTITY_MODEL_COLLECTION_MODEL_TO_ACCOUNT_TWO);
    assertEquals(
        ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_TO_ACCOUNT_TWO),
        transactionController.getAllTransactions(Map.of(TARGET_ACCOUNT_ID, TEST_ACCOUNT_TWO_ID)));
  }

  @Test
  public void whenGettingAllTransactionsFromAcc1ToAcc2_returnOnlyThoseTransactions() {
    when(transactionService.getAllTransactionsBetween(TEST_ACCOUNT_ONE_ID, TEST_ACCOUNT_TWO_ID))
        .thenReturn(List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO)); // Those are from Acc1 to Acc2.
    when(transactionModelAssembler.toCollectionModel(
            List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO),
            Map.of(
                SOURCE_ACCOUNT_ID,
                TEST_ACCOUNT_DTO_ONE.getId(),
                TARGET_ACCOUNT_ID,
                TEST_ACCOUNT_DTO_TWO.getId())))
        .thenReturn(TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE_TO_ACCOUNT_TWO);
    assertEquals(
        ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE_TO_ACCOUNT_TWO),
        transactionController.getAllTransactions(
            Map.of(SOURCE_ACCOUNT_ID, TEST_ACCOUNT_ONE_ID, TARGET_ACCOUNT_ID, TEST_ACCOUNT_TWO_ID)));
  }
}
