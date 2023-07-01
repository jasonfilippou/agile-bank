package com.agilebank.unit.controller;

import static com.agilebank.controller.TransactionController.SOURCE_ACCOUNT_ID;
import static com.agilebank.controller.TransactionController.TARGET_ACCOUNT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.TransactionController;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.exceptions.InsufficientBalanceException;
import com.agilebank.util.exceptions.InvalidAmountException;
import com.agilebank.util.exceptions.NonExistentAccountException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerUnitTests {
    
    private static final String ALL_TRANSACTIONS_BETWEEN = "all_transactions_between";
    private static final String ALL_TRANSACTIONS = "all_transactions";
    private static final String ACCOUNT_ONE_ID = "acc1";
    private static final String ACCOUNT_TWO_ID = "acc2";
    private static final String ACCOUNT_THREE_ID = "acc3";
    
    @InjectMocks
    private TransactionController transactionController;

    @Mock private TransactionService transactionService;

    @Mock private TransactionModelAssembler transactionModelAssembler = new TransactionModelAssembler();

    private static final TransactionDto TEST_TRANSACTION_DTO_ONE = new TransactionDto(ACCOUNT_ONE_ID,
            ACCOUNT_TWO_ID, new BigDecimal("20.01"), Currency.ADP);

    private static final TransactionDto TEST_TRANSACTION_DTO_TWO = new TransactionDto(ACCOUNT_ONE_ID,
            ACCOUNT_TWO_ID, new BigDecimal("190.80"), Currency.GBP);

    private static final TransactionDto TEST_TRANSACTION_DTO_THREE = new TransactionDto(ACCOUNT_ONE_ID,
            ACCOUNT_THREE_ID, new BigDecimal("200.103"), Currency.USD);

    private static final TransactionDto TEST_TRANSACTION_DTO_FOUR = new TransactionDto(ACCOUNT_THREE_ID,
            ACCOUNT_TWO_ID, new BigDecimal("0.19"), Currency.ADP);

    private static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE =
            EntityModel.of(TEST_TRANSACTION_DTO_ONE,
            linkTo(methodOn(TransactionController.class)
                    .getAllTransactions(Map.of(SOURCE_ACCOUNT_ID,
                            TEST_TRANSACTION_DTO_ONE.getSourceAccountId(),
                            TARGET_ACCOUNT_ID,
                            TEST_TRANSACTION_DTO_ONE.getTargetAccountId())))
                    .withRel(ALL_TRANSACTIONS_BETWEEN),
                    linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap())).
                            withRel(ALL_TRANSACTIONS));

    private static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO =
            EntityModel.of(TEST_TRANSACTION_DTO_TWO,
                    linkTo(methodOn(TransactionController.class)
                            .getAllTransactions(Map.of(SOURCE_ACCOUNT_ID,
                                    TEST_TRANSACTION_DTO_TWO.getSourceAccountId(),
                                    TARGET_ACCOUNT_ID,
                                    TEST_TRANSACTION_DTO_TWO.getTargetAccountId())))
                            .withRel(ALL_TRANSACTIONS_BETWEEN),
                    linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap())).
                            withRel(ALL_TRANSACTIONS));

    private static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE =
            EntityModel.of(TEST_TRANSACTION_DTO_THREE,
                    linkTo(methodOn(TransactionController.class)
                            .getAllTransactions(Map.of(SOURCE_ACCOUNT_ID,
                                    TEST_TRANSACTION_DTO_THREE.getSourceAccountId(),
                                    TARGET_ACCOUNT_ID,
                                    TEST_TRANSACTION_DTO_THREE.getTargetAccountId())))
                            .withRel(ALL_TRANSACTIONS_BETWEEN),
                    linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap())).
                            withRel(ALL_TRANSACTIONS));

    private static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR =
            EntityModel.of(TEST_TRANSACTION_DTO_FOUR,
                    linkTo(methodOn(TransactionController.class)
                            .getAllTransactions(Map.of(SOURCE_ACCOUNT_ID,
                                    TEST_TRANSACTION_DTO_FOUR.getSourceAccountId(),
                                    TARGET_ACCOUNT_ID,
                                    TEST_TRANSACTION_DTO_FOUR.getTargetAccountId())))
                            .withRel(ALL_TRANSACTIONS_BETWEEN),
                    linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap())).
                            withRel(ALL_TRANSACTIONS));
    
    private static final CollectionModel<EntityModel<TransactionDto>> TEST_ENTITY_MODEL_COLLECTION_MODEL_FULL = 
            CollectionModel.of(List.of(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO, 
                    TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE, TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR));
    
    private static final CollectionModel<EntityModel<TransactionDto>> TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACC1 = 
            CollectionModel.of(List.of(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO, 
                    TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE));

    private static final CollectionModel<EntityModel<TransactionDto>> TEST_ENTITY_MODEL_COLLECTION_MODEL_TO_ACC2 =
            CollectionModel.of(List.of(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO,
                    TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR));

    private static final CollectionModel<EntityModel<TransactionDto>> TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACC1_TO_ACC2 =
            CollectionModel.of(List.of(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO));
    @Before
    public void setUp(){
        when(transactionModelAssembler.toModel(TEST_TRANSACTION_DTO_ONE)).thenReturn(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE);
        when(transactionModelAssembler.toModel(TEST_TRANSACTION_DTO_TWO)).thenReturn(TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO);
        when(transactionModelAssembler.toModel(TEST_TRANSACTION_DTO_THREE)).thenReturn(TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE);
        when(transactionModelAssembler.toModel(TEST_TRANSACTION_DTO_FOUR)).thenReturn(TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR);
    }
    
    /* POST new transaction tests */
    
    @Test
    public void whenPostingANewTransaction_andServiceCompletesSuccessfully_thenReturnTransaction(){
        when(transactionService.storeTransaction(TEST_TRANSACTION_DTO_ONE)).thenReturn(TEST_TRANSACTION_DTO_ONE);
        assertEquals(ResponseEntity.ok(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE), transactionController.postNewTransaction(TEST_TRANSACTION_DTO_ONE));
    }
    
    @Test(expected = NonExistentAccountException.class)
    public void whenPostingANewTransaction_andServiceThrowsNonExistentAccountException_thenExceptionBubblesUp(){
        doThrow(new NonExistentAccountException("non-existent account")).when(transactionService)
                .storeTransaction(TEST_TRANSACTION_DTO_ONE);
        transactionController.postNewTransaction(TEST_TRANSACTION_DTO_ONE);
    }

    @Test(expected = InvalidAmountException.class)
    public void whenPostingANewTransaction_andServiceThrowsInvalidAmountException_thenExceptionBubblesUp(){
        doThrow(new InvalidAmountException(BigDecimal.TEN)).when(transactionService)
                .storeTransaction(TEST_TRANSACTION_DTO_ONE);
        transactionController.postNewTransaction(TEST_TRANSACTION_DTO_ONE);
    }

    @Test(expected = InsufficientBalanceException.class)
    public void whenPostingANewTransaction_andServiceInsufficientBalanceException_thenExceptionBubblesUp(){
        doThrow(new InsufficientBalanceException("accountId", BigDecimal.ZERO, Currency.GBP, BigDecimal.ONE)).when(transactionService)
                .storeTransaction(TEST_TRANSACTION_DTO_ONE);
        transactionController.postNewTransaction(TEST_TRANSACTION_DTO_ONE);
    }
    
    /* GET from / to / between / all tests */
    
    @Test
    public void whenGettingAllTransactions_returnAllTransactions(){
        when(transactionService.getAllTransactions()).thenReturn(
                List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE, TEST_TRANSACTION_DTO_FOUR)
        );
        assertEquals(ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_FULL), transactionController.getAllTransactions(Collections.emptyMap()));
    }
    
    @Test
    public void whenGettingAllTransactionsFromAcc1_returnOnlyThoseTransactions(){
        when(transactionService.getAllTransactionsFrom(ACCOUNT_ONE_ID)).thenReturn(List.of(TEST_TRANSACTION_DTO_ONE,
                TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE));
        assertEquals(ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACC1), transactionController.getAllTransactions(Map.of(SOURCE_ACCOUNT_ID, ACCOUNT_ONE_ID)));
    }

    @Test
    public void whenGettingAllTransactionsToAcc2_returnOnlyThoseTransactions(){
        when(transactionService.getAllTransactionsTo(ACCOUNT_TWO_ID)).thenReturn(List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_FOUR));
        assertEquals(ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_TO_ACC2), transactionController.getAllTransactions(Map.of(TARGET_ACCOUNT_ID, ACCOUNT_TWO_ID)));
    }
    
    @Test
    public void whenGettingAllTransactionsFromAcc1ToAcc2_returnOnlyThoseTransactions(){
        when(transactionService.getAllTransactionsBetween(ACCOUNT_ONE_ID, ACCOUNT_TWO_ID)).thenReturn(List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO));
        assertEquals(ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACC1_TO_ACC2), transactionController.getAllTransactions(Map.of(SOURCE_ACCOUNT_ID, ACCOUNT_ONE_ID, TARGET_ACCOUNT_ID, ACCOUNT_TWO_ID)));
    }
}
