package com.agilebank.unit.service.transaction;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.model.transaction.Transaction;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.persistence.TransactionRepository;
import com.agilebank.service.transaction.TransactionSanityChecker;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    // Transaction 1 is from account 1 to account 2
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getSourceAccountId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getTargetAccountId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_TWO));
    // We don't really care about the balances checking out for this test,
    // the transactionSanityChecker can do nothing.
    doNothing()
        .when(transactionSanityChecker)
        .checkTransaction(
            TEST_TRANSACTION_DTO_ONE,
            Optional.of(TEST_ACCOUNT_ONE),
            Optional.of(TEST_ACCOUNT_TWO),
            TEST_EXCHANGE_RATES);
    when(accountRepository.save(TEST_ACCOUNT_ONE)).thenReturn(TEST_ACCOUNT_ONE);
    when(accountRepository.save(TEST_ACCOUNT_TWO)).thenReturn(TEST_ACCOUNT_TWO);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(TEST_TRANSACTION_ONE);
    assertEquals(
        TEST_TRANSACTION_DTO_ONE, transactionService.storeTransaction(TEST_TRANSACTION_DTO_ONE));
  }
  
  @Test(expected = AccountNotFoundException.class)
  public void whenSanityCheckerThrowsNonExistentAccountException_thenExceptionBubblesUp(){
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getSourceAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getTargetAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_TWO));
    doThrow(new AccountNotFoundException(TEST_TRANSACTION_DTO_ONE.getSourceAccountId())).when(transactionSanityChecker).checkTransaction(
            TEST_TRANSACTION_DTO_ONE, Optional.of(TEST_ACCOUNT_ONE), Optional.of(TEST_ACCOUNT_TWO), TEST_EXCHANGE_RATES);
    transactionService.storeTransaction(TEST_TRANSACTION_DTO_ONE);
  }

  @Test(expected = InvalidAmountException.class)
  public void whenSanityCheckerThrowsInvalidAmountException_thenExceptionBubblesUp(){
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getSourceAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getTargetAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_TWO));
    doThrow(new InvalidAmountException(BigDecimal.ZERO))
        .when(transactionSanityChecker)
        .checkTransaction(
            TEST_TRANSACTION_DTO_ONE,
            Optional.of(TEST_ACCOUNT_ONE),
            Optional.of(TEST_ACCOUNT_TWO),
            TEST_EXCHANGE_RATES);
    transactionService.storeTransaction(TEST_TRANSACTION_DTO_ONE);
  }

  @Test(expected = InsufficientBalanceException.class)
  public void whenSanityCheckerThrowsInsufficientBalanceException_thenExceptionBubblesUp(){
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getSourceAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    when(accountRepository.findById(TEST_TRANSACTION_DTO_ONE.getTargetAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_TWO));
    doThrow(new InsufficientBalanceException(TEST_ACCOUNT_ONE.getId(),
            BigDecimal.ZERO, TEST_ACCOUNT_ONE.getCurrency(), TEST_TRANSACTION_DTO_ONE.getAmount()))
        .when(transactionSanityChecker)
        .checkTransaction(
            TEST_TRANSACTION_DTO_ONE,
            Optional.of(TEST_ACCOUNT_ONE),
            Optional.of(TEST_ACCOUNT_TWO),
            TEST_EXCHANGE_RATES);
    transactionService.storeTransaction(TEST_TRANSACTION_DTO_ONE);
  }

  @Test(expected = SameAccountException.class)
  public void whenSanityCheckerThrowsSameAccountException_thenExceptionBubblesUp(){
    // Transaction 5 is from account 2 to account 2.
    when(accountRepository.findById(TEST_TRANSACTION_DTO_FIVE.getSourceAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_TWO));
    when(accountRepository.findById(TEST_TRANSACTION_DTO_FIVE.getTargetAccountId()))
            .thenReturn(Optional.of(TEST_ACCOUNT_TWO));
    doThrow(new SameAccountException(TEST_ACCOUNT_TWO.getId()))
            .when(transactionSanityChecker)
            .checkTransaction(
                    TEST_TRANSACTION_DTO_FIVE,
                    Optional.of(TEST_ACCOUNT_TWO),
                    Optional.of(TEST_ACCOUNT_TWO),
                    TEST_EXCHANGE_RATES);
    transactionService.storeTransaction(TEST_TRANSACTION_DTO_FIVE);
  }
  
  /* Getting transaction tests */
  
  @Test
  public void whenGettingAStoredTransactionById_thenTransactionIsReturned(){
    when(transactionRepository.findById(TEST_TRANSACTION_DTO_ONE.getId())).thenReturn(Optional.of(TEST_TRANSACTION_ONE));
    assertEquals(TEST_TRANSACTION_DTO_ONE, transactionService.getTransaction(TEST_TRANSACTION_DTO_ONE.getId()));
  }
  
  @Test(expected = TransactionNotFoundException.class)
  public void whenGettingAMissingTransaction_thenATransactionNotFoundExceptionIsThrown(){
    when(transactionRepository.findById(TEST_TRANSACTION_DTO_ONE.getId())).thenReturn(Optional.empty());
    transactionService.getTransaction(TEST_TRANSACTION_DTO_ONE.getId());
  }
  
  /* Getting all / from / to / between tests */
  @Test
  public void whenGettingAllTransactions_AllTransactionsAreReturned(){
    when(transactionRepository.findAll()).thenReturn(List.of(TEST_TRANSACTION_ONE,
            TEST_TRANSACTION_TWO, TEST_TRANSACTION_THREE, TEST_TRANSACTION_FOUR));
    assertTrue(CollectionUtils.isEqualCollection(transactionService.getAllTransactions(),
            List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE,
                    TEST_TRANSACTION_DTO_FOUR)));
  }
  
  @Test
  public void whenGettingAllTransactionsFromASourceAccount_thenOnlyThoseAreReturned(){
    // Account 1 is the source of transactions 1, 2 and 3.
    when(transactionRepository.findBySourceAccountId(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(
            List.of(TEST_TRANSACTION_ONE, TEST_TRANSACTION_TWO, TEST_TRANSACTION_THREE));
    assertTrue(CollectionUtils.isEqualCollection(transactionService.getAllTransactionsFrom(TEST_ACCOUNT_DTO_ONE.getId()),
            List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE)));
  }
  
  @Test
  public void whenGettingAllTransactionsToATargetAccount_thenOnlyThoseAreReturned(){
    // Account 2 is the target of transactions 1, 2 and 4.
    when(transactionRepository.findByTargetAccountId(TEST_ACCOUNT_DTO_TWO.getId())).thenReturn(
            List.of(TEST_TRANSACTION_ONE, TEST_TRANSACTION_TWO, TEST_TRANSACTION_THREE));
    assertTrue(CollectionUtils.isEqualCollection(transactionService.getAllTransactionsTo(TEST_ACCOUNT_DTO_TWO.getId()),
            List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO, TEST_TRANSACTION_DTO_THREE)));
  }
  
  @Test
  public void whenGettingAllTransactionsBetweenAGivenSourceAndAGivenTargetAccount_thenOnlyThoseAreReturned(){
    // Transactions 1 and 2 are both from account 1 to account 2.
    when(transactionRepository.findBySourceAccountIdAndTargetAccountId(TEST_ACCOUNT_DTO_ONE.getId(),
            TEST_ACCOUNT_DTO_TWO.getId())).thenReturn(List.of(TEST_TRANSACTION_ONE, TEST_TRANSACTION_TWO));
    assertTrue(CollectionUtils.isEqualCollection(transactionService.getAllTransactionsBetween(TEST_ACCOUNT_DTO_ONE.getId(),
            TEST_ACCOUNT_DTO_TWO.getId()), List.of(TEST_TRANSACTION_DTO_ONE, TEST_TRANSACTION_DTO_TWO)));
  }
  
  @Test
  public void whenDeletingATransactionThatExistsInRepo_thenOk(){
    when(transactionRepository.findById(TEST_TRANSACTION_DTO_ONE.getId())).thenReturn(Optional.of(TEST_TRANSACTION_ONE));
    Throwable expected = null;
    try {
      transactionService.deleteTransaction(TEST_TRANSACTION_DTO_ONE.getId());
    } catch(Throwable thrown){
      expected = thrown;
    }
    assertNull(expected, "Expected nothing to be thrown by service method.");
  }
  
  @Test(expected = TransactionNotFoundException.class)
  public void whenDeletingATransactionThatDoesNotExistInRepo_ThenTransactionNotFoundExceptionIsThrown(){
    when(transactionRepository.findById(TEST_TRANSACTION_DTO_ONE.getId())).thenReturn(Optional.empty());
    transactionService.deleteTransaction(TEST_TRANSACTION_DTO_ONE.getId());
  }
  
  @Test
  public void whenDeletingAllTransactions_thenOk(){
    doNothing().when(transactionRepository).deleteAll();
    Throwable expected = null;
    try {
      transactionService.deleteAllTransactions();
    } catch(Throwable thrown){
      expected = thrown;
    }
    assertNull(expected, "Expected nothing to be thrown by service method.");
  }
}
