package com.agilebank.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.model.currency.Currency;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.exceptions.AccountAlreadyExistsException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class AccountControllerUnitTests {

  @InjectMocks private AccountController accountController;

  @Mock private AccountService accountService;

  @Mock private AccountModelAssembler accountModelAssembler = new AccountModelAssembler();

  private static final AccountDto TEST_ACCOUNT_DTO_ONE =
      new AccountDto("accountOne", new BigDecimal("1400.25"), Currency.GBP);

  private static final AccountDto TEST_ACCOUNT_DTO_TWO =
          new AccountDto("accountTwo", new BigDecimal("801.01"), Currency.IDR);

  private static final AccountDto TEST_ACCOUNT_DTO_THREE =
          new AccountDto("accountThree", new BigDecimal("50.00"), Currency.USD);

  private static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE =
          EntityModel.of(
                  TEST_ACCOUNT_DTO_ONE,
                  linkTo(methodOn(AccountController.class).getAccount(TEST_ACCOUNT_DTO_ONE.getId()))
                          .withSelfRel(),
                  linkTo(methodOn(AccountController.class).getAllAccounts())
                          .withRel("all_accounts"));

  private static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_TWO =
          EntityModel.of(
                  TEST_ACCOUNT_DTO_TWO,
                  linkTo(methodOn(AccountController.class).getAccount(TEST_ACCOUNT_DTO_TWO.getId()))
                          .withSelfRel(),
                  linkTo(methodOn(AccountController.class).getAllAccounts())
                          .withRel("all_accounts"));

  private static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_THREE =
          EntityModel.of(
                  TEST_ACCOUNT_DTO_ONE,
                  linkTo(methodOn(AccountController.class).getAccount(TEST_ACCOUNT_DTO_THREE.getId()))
                          .withSelfRel(),
                  linkTo(methodOn(AccountController.class).getAllAccounts())
                          .withRel("all_accounts"));
  
  private static final CollectionModel<EntityModel<AccountDto>> TEST_ENTITY_MODEL_COLLECTION_MODEL = 
          CollectionModel.of(List.of(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE, TEST_ACCOUNT_DTO_ENTITY_MODEL_TWO, 
                  TEST_ACCOUNT_DTO_ENTITY_MODEL_THREE), linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel());
  
  @Before
  public void setUp() {
    when(accountModelAssembler.toModel(TEST_ACCOUNT_DTO_ONE))
        .thenReturn(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE);
    when(accountModelAssembler.toModel(TEST_ACCOUNT_DTO_TWO))
            .thenReturn(TEST_ACCOUNT_DTO_ENTITY_MODEL_TWO);
    when(accountModelAssembler.toModel(TEST_ACCOUNT_DTO_THREE))
            .thenReturn(TEST_ACCOUNT_DTO_ENTITY_MODEL_THREE);
  }

  @Test
  public void whenPostingNewAccount_andServiceStoresSuccessfully_accountIsReturned(){
      when(accountService.storeAccount(TEST_ACCOUNT_DTO_ONE)).thenReturn(TEST_ACCOUNT_DTO_ONE);
      assertEquals(ResponseEntity.ok(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE), 
              accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE));
  }
  
  @Test(expected = AccountAlreadyExistsException.class)
  public void whenPostingNewAccount_andServiceThrowsAccountAlreadyExistsException_thenExceptionBubblesUp(){
    doThrow(new AccountAlreadyExistsException(TEST_ACCOUNT_DTO_ONE.getId())).when(accountService).storeAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
  }
  
  @Test
  public void whenGettingAllAccounts_thenResponseEntityOverCollectionModelReturned(){
    when(accountService.getAllAccounts()).thenReturn(List.of(TEST_ACCOUNT_DTO_ONE, TEST_ACCOUNT_DTO_TWO, TEST_ACCOUNT_DTO_THREE));
    assertEquals(ResponseEntity.ok(TEST_ENTITY_MODEL_COLLECTION_MODEL), accountController.getAllAccounts());
  }
  
  @Test
  public void whenGettingAnAccountThatExists_thenAccountIsReturned(){
    when(accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(TEST_ACCOUNT_DTO_ONE);
    assertEquals(ResponseEntity.ok(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE), accountController.getAccount(TEST_ACCOUNT_DTO_ONE.getId()));
  }
  
  @Test(expected = NonExistentAccountException.class)
  public void whenGettingAnAccountThatDoesNotExst_thenNonExistentAccountIsThrown(){
    doThrow(new NonExistentAccountException("someAccountId")).when(accountService).getAccount(anyString());
    accountController.getAccount(RandomStringUtils.randomAlphanumeric(10));
  }
}
