package com.agilebank.unit.controller;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.agilebank.controller.AccountController;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.exceptions.AccountAlreadyExistsException;
import com.agilebank.util.exceptions.InvalidBalanceException;
import com.agilebank.util.exceptions.NonExistentAccountException;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
      assertEquals(ResponseEntity.ok(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE), 
              accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE));
  }
  
  @Test(expected = InvalidBalanceException.class)
  public void whenPostingNewAccountWithNonPositiveBalance_andServiceThrowsInvalidBalanceException_thenExceptionBubblesUp(){
    doThrow(new InvalidBalanceException(TEST_ACCOUNT_DTO_ONE.getId(), BigDecimal.ZERO))
        .when(accountService)
        .storeAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
  }
  
  @Test(expected = AccountAlreadyExistsException.class)
  public void whenPostingNewAccount_andServiceThrowsAccountAlreadyExistsException_thenExceptionBubblesUp(){
    doThrow(new AccountAlreadyExistsException(TEST_ACCOUNT_DTO_ONE.getId())).when(accountService).storeAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
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
  
  @Test(expected = NonExistentAccountException.class)
  public void whenGettingAnAccountThatDoesNotExist_thenNonExistentAccountIsThrown(){
    doThrow(new NonExistentAccountException("someAccountId")).when(accountService).getAccount(anyString());
    accountController.getAccount(RandomStringUtils.randomAlphanumeric(10));
  }
}
