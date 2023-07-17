package com.agilebank.unit.controller;

import static com.agilebank.util.Constants.*;
import static com.agilebank.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.model.currency.Currency;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.PaginationTester;
import com.agilebank.util.SortOrder;
import com.agilebank.util.exceptions.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerUnitTests {

  @InjectMocks private AccountController accountController;

  @Mock private AccountService accountService;

  @Mock private static AccountModelAssembler accountModelAssembler = new AccountModelAssembler();

  private static final AccountDto TEST_ACCOUNT_DTO_ONE = TEST_ACCOUNT_DTOS.get(0);
  private static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE = TEST_ACCOUNT_DTO_ENTITY_MODELS.get(0);

  @BeforeAll
  public static void setUp() {
    for(int i = 0; i < TEST_ACCOUNT_DTOS.size(); i++){
      when(accountModelAssembler.toModel(TEST_ACCOUNT_DTOS.get(i)))
              .thenReturn(TEST_ACCOUNT_DTO_ENTITY_MODELS.get(i));
    }
  }

  /* POST tests */

  @Test
  public void whenPostingNewAccount_andServiceStoresSuccessfully_accountIsReturned() {
    when(accountService.storeAccount(TEST_ACCOUNT_DTO_ONE)).thenReturn(TEST_ACCOUNT_DTO_ONE);
    assertEquals(
        new ResponseEntity<>(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE, HttpStatus.CREATED),
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE));
  }

  /* GET ALL tests */

  @Test
  public void whenGettingAllAccountsInPage_thenExactlyThoseAccountsAreReturned() {
    // There are 20 accounts total.

    // First, ask for a page with all 20 accounts.
    PaginationTester.builder()
            .pojoType(AccountDto.class)
            .totalPages(1)
            .pageSize(20)
            .build()
            .runTest(this::testAggregateGetForGivenParameters);
    
    // Now, ask for 4 of 5 accounts each.
    PaginationTester.builder()
            .pojoType(AccountDto.class)
            .totalPages(4)
            .pageSize(5)
            .build()
            .runTest(this::testAggregateGetForGivenParameters);
    

    // Now, 4 of 6 accounts each, except for the last one, which will have 2 since 20 = 3 * 6 + 2.
    PaginationTester.builder()
            .pojoType(AccountDto.class)
            .totalPages(4)
            .pageSize(6)
            .build()
            .runTest(this::testAggregateGetForGivenParameters);
    
    // Finally, 20 pages of 1 account each.
    PaginationTester.builder()
            .pojoType(AccountDto.class)
            .totalPages(20)
            .pageSize(1)
            .build()
            .runTest(this::testAggregateGetForGivenParameters);
  }
  
    private void testAggregateGetForGivenParameters(AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords){
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    List<AccountDto> subListOfPage = TEST_ACCOUNT_DTOS.stream().sorted((a1, a2) -> compareFieldsInGivenOrder(a1.getClass(), a2.getClass(),
            sortByField, sortOrder)).collect(Collectors.toList()).subList(page * pageSize, pageSize * (page + 1));
    when(accountService.getAllAccounts(page, pageSize, sortByField, sortOrder)).thenReturn(new PageImpl<>(subListOfPage));
    when(accountModelAssembler.toCollectionModel(
              new PageImpl<>(subListOfPage)))
              .thenReturn(accountDtosToCollectionModel(subListOfPage));
    ResponseEntity<CollectionModel<EntityModel<AccountDto>>> responseEntity =
        accountController.getAllAccounts(page, pageSize, sortByField, sortOrder);
    Collection<AccountDto> accountDtos = Objects.requireNonNull(responseEntity.getBody())
            .getContent().stream().map(EntityModel::getContent).toList();
    assertEquals(accountDtos.size(), expectedNumberOfRecords);
    assertTrue(
        collectionIsSortedByFieldInGivenDirection(
            accountDtos, aggregateGetQueryParams.getSortByField(), aggregateGetQueryParams.getSortOrder()));
  }

  /* GET by ID tests */
  @Test
  public void whenGettingAnAccountThatExists_thenAccountIsReturned() {
    when(accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(TEST_ACCOUNT_DTO_ONE);
    assertEquals(
        ResponseEntity.ok(TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE),
        accountController.getAccount(TEST_ACCOUNT_DTO_ONE.getId()));
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenGettingAnAccountThatDoesNotExist_thenNonExistentAccountIsThrown() {
    doThrow(new AccountNotFoundException(0L)).when(accountService).getAccount(0L);
    accountController.getAccount(0L);
  }

  /* DELETE tests */

  @Test
  public void whenDeletingAnAccountThatExists_thenNoContentIsReturned() {
    doNothing().when(accountService).deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
    assertEquals(
        ResponseEntity.noContent().build(),
        accountController.deleteAccount(TEST_ACCOUNT_DTO_ONE.getId()));
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenDeletingAnAccountThatDoesNotExist_thenAccountNotFoundExceptionIsThrown() {
    doThrow(new AccountNotFoundException(TEST_ACCOUNT_DTO_ONE.getId()))
        .when(accountService)
        .deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
    accountController.deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
  }

  /* PUT tests */

  @Test
  public void whenDeletingAllAccounts_thenNoContentIsReturned() {
    doNothing().when(accountService).deleteAllAccounts();
    assertEquals(ResponseEntity.noContent().build(), accountController.deleteAllAccounts());
  }

  @Test
  public void whenServiceReplacesSuccessfully_thenOk() {
    final Long id = 1L;
    AccountDto accountDtoPartial =
        AccountDto.builder().currency(Currency.AED).balance(BigDecimal.TEN).build();
    AccountDto accountDtoFull =
        AccountDto.builder()
            .id(id)
            .currency(accountDtoPartial.getCurrency())
            .balance(accountDtoPartial.getBalance())
            .build();
    when(accountService.replaceAccount(id, accountDtoPartial)).thenReturn(accountDtoFull);
    when(accountModelAssembler.toModel(accountDtoFull))
        .thenReturn(
            EntityModel.of(
                accountDtoFull,
                linkTo(methodOn(AccountController.class).getAccount(accountDtoFull.getId()))
                    .withSelfRel(),
                linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
                    .withRel("all_accounts")));
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.replaceAccount(id, accountDtoPartial);
    assertEquals(accountDtoFull, Objects.requireNonNull(responseEntity.getBody()).getContent());
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
  }

  /* PATCH tests */

  @Test
  public void whenUpdatingAnExistingAccountWithANewBalance_thenNewAccountInfoIsReturned() {
    AccountDto newAccountInfo =
            AccountDto.builder()
                    .id(TEST_ACCOUNT_DTO_ONE.getId())
                    .balance(
                            TEST_ACCOUNT_DTO_ONE.getBalance().add(BigDecimal.TEN)) // Ensuring balance different
                    .build();
    AccountDto patchedAccountDto =
            AccountDto.builder()
                    .id(newAccountInfo.getId())
                    .balance(newAccountInfo.getBalance())
                    .currency(TEST_ACCOUNT_DTO_ONE.getCurrency())
                    .build();
    EntityModel<AccountDto> patchedAccountDtoEntityModel = EntityModel.of(
            patchedAccountDto,
            linkTo(methodOn(AccountController.class).getAccount(patchedAccountDto.getId()))
                    .withSelfRel(),
            linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX),
                    Integer.parseInt(DEFAULT_PAGE_SIZE),
                    DEFAULT_SORT_BY_FIELD,
                    SortOrder.ASC))
                    .withRel("all_accounts"));
    when(accountService.updateAccount(newAccountInfo.getId(), newAccountInfo)).thenReturn(patchedAccountDto);
    when(accountModelAssembler.toModel(patchedAccountDto)).thenReturn(patchedAccountDtoEntityModel);
    assertEquals(
            ResponseEntity.ok(patchedAccountDtoEntityModel),
            accountController.updateAccount(newAccountInfo.getId(), newAccountInfo));
  }

  @Test
  public void whenUpdatingAnExistingAccountWithANewCurrency_thenNewAccountInfoIsReturned() {
    AccountDto newAccountInfo =
            AccountDto.builder()
                    .id(TEST_ACCOUNT_DTO_ONE.getId())
                    .balance(TEST_ACCOUNT_DTO_ONE.getBalance())
                    .currency(Currency.AMD) // TEST_ACCOUNT_DTO_ONE has GBP
                    .build();
    AccountDto patchedAccountDto =
            AccountDto.builder()
                    .id(newAccountInfo.getId())
                    .balance(TEST_ACCOUNT_DTO_ONE.getBalance())
                    .currency(newAccountInfo.getCurrency())
                    .build();
    EntityModel<AccountDto> patchedAccountDtoEntityModel = EntityModel.of(
            patchedAccountDto,
            linkTo(methodOn(AccountController.class).getAccount(patchedAccountDto.getId()))
                    .withSelfRel(),
            linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX),
                    Integer.parseInt(DEFAULT_PAGE_SIZE),
                    DEFAULT_SORT_BY_FIELD,
                    SortOrder.ASC))
                    .withRel("all_accounts"));
    when(accountService.updateAccount(newAccountInfo.getId(), newAccountInfo)).thenReturn(patchedAccountDto);
    when(accountModelAssembler.toModel(patchedAccountDto)).thenReturn(patchedAccountDtoEntityModel);
    assertEquals(
            ResponseEntity.ok(patchedAccountDtoEntityModel),
            accountController.updateAccount(newAccountInfo.getId(), newAccountInfo));
  }
}
