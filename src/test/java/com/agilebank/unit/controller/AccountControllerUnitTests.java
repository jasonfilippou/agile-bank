package com.agilebank.unit.controller;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.agilebank.controller.AccountController;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.exceptions.AccountNotFoundException;
import com.agilebank.util.exceptions.InvalidBalanceException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerUnitTests {

  @InjectMocks private AccountController accountController;

  @Mock private AccountService accountService;

  @Mock private AccountModelAssembler accountModelAssembler = new AccountModelAssembler();
  
  
  @Before
  public void setUp() {
    when(accountModelAssembler.toModel(TEST_ACCOUNT_DTO_ONE))
        .thenReturn(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE);
  }

  @Test
  public void whenPostingNewAccount_andServiceStoresSuccessfully_accountIsReturned(){
      when(accountService.storeAccount(TEST_ACCOUNT_DTO_ONE)).thenReturn(TEST_ACCOUNT_DTO_ONE);
    assertEquals(
        new ResponseEntity<>(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE, HttpStatus.CREATED),
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE));
  }
  
  @Test(expected = InvalidBalanceException.class)
  public void whenPostingNewAccountWithNonPositiveBalance_andServiceThrowsInvalidBalanceException_thenExceptionBubblesUp(){
    doThrow(new InvalidBalanceException(BigDecimal.ZERO))
        .when(accountService)
        .storeAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
  }
  
  @Test
  public void whenGettingAllAccounts_thenResponseEntityOverCollectionModelReturned(){
    when(accountService.getAllAccounts()).thenReturn(List.of(TEST_ACCOUNT_DTO_ONE, TEST_ACCOUNT_DTO_TWO, TEST_ACCOUNT_DTO_THREE));
    when(accountModelAssembler.toCollectionModel(List.of(TEST_ACCOUNT_DTO_ONE,
            TEST_ACCOUNT_DTO_TWO, TEST_ACCOUNT_DTO_THREE))).thenReturn(TEST_ENTITY_MODEL_COLLECTION_MODEL);
    assertEquals(ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL), accountController.getAllAccounts());
  }
  
  @Test
  public void whenGettingAnAccountThatExists_thenAccountIsReturned(){
    when(accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(TEST_ACCOUNT_DTO_ONE);
    assertEquals(ResponseEntity.ok(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE), accountController.getAccount(TEST_ACCOUNT_DTO_ONE.getId()));
  }
  
  @Test(expected = AccountNotFoundException.class)
  public void whenGettingAnAccountThatDoesNotExist_thenNonExistentAccountIsThrown(){
    doThrow(new AccountNotFoundException(0L)).when(accountService).getAccount(0L);
    accountController.getAccount(0L);
  }
  
  @Test
  public void whenDeletingAnAccountThatExists_thenNoContentIsReturned(){
    doNothing().when(accountService).deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
    assertEquals(ResponseEntity.noContent().build(), accountController.deleteAccount(TEST_ACCOUNT_DTO_ONE.getId()));
  }
  
  @Test(expected = AccountNotFoundException.class)
  public void whenDeletingAnAccountThatDoesNotExist_thenAccountNotFoundExceptionIsThrown(){
    doThrow(new AccountNotFoundException(TEST_ACCOUNT_DTO_ONE.getId())).when(accountService).deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
    accountController.deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
  }
  @Test
  public void whenDeletingAllAccounts_thenNoContentIsReturned(){
    doNothing().when(accountService).deleteAllAccounts();
    assertEquals(ResponseEntity.noContent().build(), accountController.deleteAllAccounts());

  }
}
