package com.agilebank.integration;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;
import static com.agilebank.util.Constants.*;
import static com.agilebank.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.agilebank.controller.AccountController;
import com.agilebank.controller.CurrencyLedgerController;
import com.agilebank.controller.TransactionController;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.PaginationTester;
import com.agilebank.util.SortOrder;
import com.agilebank.util.exceptions.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@NoArgsConstructor
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class AgileBankIntegrationTests {

  @Autowired private AccountController accountController;
  @Autowired private TransactionController transactionController;
  @Autowired private AccountModelAssembler accountModelAssembler;
  @Autowired private TransactionModelAssembler transactionModelAssembler;
  @Autowired private CurrencyLedgerController currencyLedgerController;

  @MockBean
  private CurrencyLedger
      currencyLedger; // In transaction GET ALL tests, we will need to mock this dependency.

  private static final AccountDto TEST_ACCOUNT_DTO_ONE = TEST_ACCOUNT_DTOS.get(0);
  private static final AccountDto TEST_ACCOUNT_DTO_TWO = TEST_ACCOUNT_DTOS.get(1);

  private static final Random RANDOM = new Random(47);

  /* Tests exclusively for accounts first. */

  @Test
  public void whenPostingAValidAccount_accountCanThenBeFound() {
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto accountDto = Objects.requireNonNull(responseEntity.getBody()).getContent();
    assert accountDto != null;
    assertEquals(
        ResponseEntity.ok(accountModelAssembler.toModel(TEST_ACCOUNT_DTO_ONE)),
        accountController.getAccount(accountDto.getId()));
  }

  @Test
  public void whenPostingTwoValidAccounts_getAllFindsThemBoth() {
    ResponseEntity<EntityModel<AccountDto>> responseEntityOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountDtoOne = Objects.requireNonNull(responseEntityOne.getBody()).getContent();
    AccountDto accountDtoTwo = Objects.requireNonNull(responseEntityTwo.getBody()).getContent();
    assert accountDtoOne != null && accountDtoTwo != null;
    assertEquals(
        ResponseEntity.ok(
            CollectionModel.of(
                Stream.of(
                        AccountDto.builder()
                            .id(accountDtoOne.getId())
                            .balance(TEST_ACCOUNT_DTO_ONE.getBalance())
                            .currency(TEST_ACCOUNT_DTO_ONE.getCurrency())
                            .build(),
                        AccountDto.builder()
                            .id(accountDtoTwo.getId())
                            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
                            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
                            .build())
                    .map(accountModelAssembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(
                        methodOn(AccountController.class)
                            .getAllAccounts(
                                Integer.parseInt(DEFAULT_PAGE_IDX),
                                Integer.parseInt(DEFAULT_PAGE_SIZE),
                                DEFAULT_SORT_BY_FIELD,
                                SortOrder.ASC))
                    .withSelfRel())),
        accountController.getAllAccounts(
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC));
  }

  @Test
  public void whenPostingAllAccounts_thenPaginationAndSortingReturnsExpectedPages() {
    TEST_ACCOUNT_DTOS.forEach(accountController::postAccount);

    // There are 20 accounts total.

    // First, ask for a page with all 20 accounts.
    PaginationTester.builder()
        .pojoType(AccountDto.class)
        .totalPages(1)
        .pageSize(20)
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfAccounts);

    // Now, ask for 4 of 5 accounts each.
    PaginationTester.builder()
        .pojoType(AccountDto.class)
        .totalPages(4)
        .pageSize(5)
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfAccounts);

    // Now, 4 of 6 accounts each, except for the last one, which will have 2 since 20 = 3 * 6 + 2.
    PaginationTester.builder()
        .pojoType(AccountDto.class)
        .totalPages(4)
        .pageSize(6)
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfAccounts);

    // Finally, 20 pages of 1 account each.
    PaginationTester.builder()
        .pojoType(AccountDto.class)
        .totalPages(20)
        .pageSize(1)
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfAccounts);
  }

  private void testSortedAndPaginatedAggregateGetOfAccounts(
      AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    List<AccountDto> expectedAccounts =
        TEST_ACCOUNT_DTOS.stream()
            .sorted(
                (a1, a2) ->
                    compareFieldsInGivenOrder(a1.getClass(), a2.getClass(), sortByField, sortOrder))
            .toList()
            .subList(page * pageSize, pageSize * (page + 1));
    assertEquals(
        ResponseEntity.ok(
            CollectionModel.of(
                expectedAccounts.stream()
                    .map(accountModelAssembler::toModel)
                    .collect(Collectors.toList()),
                linkTo(
                        methodOn(AccountController.class)
                            .getAllAccounts(
                                Integer.parseInt(DEFAULT_PAGE_IDX),
                                Integer.parseInt(DEFAULT_PAGE_SIZE),
                                DEFAULT_SORT_BY_FIELD,
                                SortOrder.ASC))
                    .withSelfRel())),
        accountController.getAllAccounts(page, pageSize, sortByField, sortOrder));
  }


  @Test
  public void whenGettingAllAccountsWithoutHavingPostedAny_themEmptyListIsReturned() {
    assertEquals(
        ResponseEntity.ok(
            CollectionModel.of(
                Collections.emptyList(),
                linkTo(
                        methodOn(AccountController.class)
                            .getAllAccounts(
                                Integer.parseInt(DEFAULT_PAGE_IDX),
                                Integer.parseInt(DEFAULT_PAGE_SIZE),
                                DEFAULT_SORT_BY_FIELD,
                                SortOrder.ASC))
                    .withSelfRel())),
        accountController.getAllAccounts(
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC));
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenGettingAnAccountThatWeHaveNotPosted_thenNonExistentAccountExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto accountDto = Objects.requireNonNull(responseEntity.getBody()).getContent();
    assert accountDto != null;
    accountController.getAccount(accountDto.getId() + 1);
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenPostingAnAccountAndThenDeletingIt_thenAccountCanNoLongerBeFound() {
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountTwoDto =
        Objects.requireNonNull(responseEntityForAccountTwo.getBody()).getContent();
    assert accountTwoDto != null;
    assertEquals(
        ResponseEntity.noContent().build(), accountController.deleteAccount(accountTwoDto.getId()));
    ResponseEntity<CollectionModel<EntityModel<AccountDto>>> responseEntityForAllAccounts =
        accountController.getAllAccounts(
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC);
    assertFalse(
        Objects.requireNonNull(responseEntityForAllAccounts.getBody()).getContent().stream()
            .map(EntityModel::getContent)
            .toList()
            .contains(accountTwoDto));
    accountController.getAccount(accountTwoDto.getId()); // This call should throw
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenPostingAnAccountAndThenDeletingItTwice_thenAccountNotFoundExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto accountOneDto =
        Objects.requireNonNull(responseEntityForAccountOne.getBody()).getContent();
    assert accountOneDto != null;
    assertEquals(
        ResponseEntity.noContent().build(), accountController.deleteAccount(accountOneDto.getId()));
    accountController.deleteAccount(accountOneDto.getId()); // This call should throw
  }

  @Test
  public void whenDeletingAllAccounts_thenNoContentIsReturnedAndNoAccountsCanBeFound() {
    accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    assertEquals(ResponseEntity.noContent().build(), accountController.deleteAllAccounts());
    ResponseEntity<CollectionModel<EntityModel<AccountDto>>> allAccountsResponseEntity =
        accountController.getAllAccounts(
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC);
    assertEquals(
        Objects.requireNonNull(allAccountsResponseEntity.getBody()).getContent().size(), 0);
  }

  @Test
  public void whenReplacingAnExistingAccount_thenReplacedAccountIsReturned() {
    ResponseEntity<EntityModel<AccountDto>> postResponseEntity =
        accountController.postAccount(
            AccountDto.builder()
                .balance(TEST_ACCOUNT_DTO_ONE.getBalance())
                .currency(TEST_ACCOUNT_DTO_ONE.getCurrency())
                .build());
    AccountDto postedAccountDto = Objects.requireNonNull(postResponseEntity.getBody()).getContent();
    assert postedAccountDto != null;
    AccountDto replacedAccountDto =
        AccountDto.builder()
            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
            .build();
    ResponseEntity<EntityModel<AccountDto>> putResponseEntity =
        accountController.replaceAccount(postedAccountDto.getId(), replacedAccountDto);
    assertEquals(
        AccountDto.builder()
            .id(postedAccountDto.getId())
            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
            .build(),
        Objects.requireNonNull(putResponseEntity.getBody()).getContent());
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenReplacingANonExistingAccount_thenAnAccountNotFoundExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> postResponseEntity =
        accountController.postAccount(
            AccountDto.builder()
                .balance(TEST_ACCOUNT_DTO_ONE.getBalance())
                .currency(TEST_ACCOUNT_DTO_ONE.getCurrency())
                .build());
    AccountDto postedAccountDto = Objects.requireNonNull(postResponseEntity.getBody()).getContent();
    assert postedAccountDto != null;
    AccountDto replacedAccountDto =
        AccountDto.builder()
            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
            .build();
    accountController.replaceAccount(postedAccountDto.getId() + 1, replacedAccountDto);
  }

  @Test
  public void whenUpdatingAnAccountWithANewBalance_thenUpdatedAccountIsReturned() {
    // POST the first test account
    ResponseEntity<EntityModel<AccountDto>> responseEntityForPost =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto postedAccountDto =
        Objects.requireNonNull(responseEntityForPost.getBody()).getContent();
    assert postedAccountDto != null;

    // Now PATCH it
    AccountDto patch =
        AccountDto.builder()
            .id(postedAccountDto.getId())
            .balance(postedAccountDto.getBalance().add(BigDecimal.TEN))
            .build();
    ResponseEntity<EntityModel<AccountDto>> responseEntityForPatch =
        accountController.updateAccount(patch.getId(), patch);
    AccountDto patchedAccountDto =
        Objects.requireNonNull(responseEntityForPatch.getBody()).getContent();
    assert patchedAccountDto != null;

    // And see if what is returned makes sense.
    assertEquals(
        AccountDto.builder()
            .id(patch.getId())
            .balance(patch.getBalance())
            .currency(postedAccountDto.getCurrency())
            .build(),
        patchedAccountDto);
  }

  @Test
  public void whenUpdatingAnAccountWithANewCurrency_thenUpdatedAccountIsReturned() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    when(currencyLedger.convertAmountToTargetCurrency(
            any(Currency.class), any(Currency.class), any(BigDecimal.class)))
        .thenCallRealMethod();
    // POST the first test account
    ResponseEntity<EntityModel<AccountDto>> responseEntityForPost =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto postedAccountDto =
        Objects.requireNonNull(responseEntityForPost.getBody()).getContent();
    assert postedAccountDto != null;

    // Now PATCH it
    AccountDto patch =
        AccountDto.builder()
            .id(postedAccountDto.getId())
            .currency(Currency.AMD) // TEST_ACCOUNT_DTO_ONE has USD
            .build();
    ResponseEntity<EntityModel<AccountDto>> responseEntityForPatch =
        accountController.updateAccount(patch.getId(), patch);
    AccountDto patchedAccountDto =
        Objects.requireNonNull(responseEntityForPatch.getBody()).getContent();
    assert patchedAccountDto != null;

    // And see if what is returned makes sense. Remember to bring the currency exchange rate into
    // the mix
    // since we are updating the currency in this test.
    ResponseEntity<Map<CurrencyPair, BigDecimal>> currencyResponseEntity =
        currencyLedgerController.getCurrencyExchangeRate(
            TEST_ACCOUNT_DTO_ONE.getCurrency(), patch.getCurrency(), null, null);
    BigDecimal exchangeRate =
        Objects.requireNonNull(currencyResponseEntity.getBody())
            .get(CurrencyPair.of(TEST_ACCOUNT_DTO_ONE.getCurrency(), patch.getCurrency()));

    assertEquals(
        AccountDto.builder()
            .id(patch.getId())
            .balance(TEST_ACCOUNT_DTO_ONE.getBalance().multiply(exchangeRate))
            .currency(patch.getCurrency())
            .build(),
        patchedAccountDto);
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenUpdatingAnAccountThatHasNotBeenPosted_thenAccountNotFoundExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> postResponseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto postedAccountDto = Objects.requireNonNull(postResponseEntity.getBody()).getContent();
    assert postedAccountDto != null;
    Long postedAccountDtoId = postedAccountDto.getId();
    AccountDto patchToNonExistentAccount =
        AccountDto.builder().id(postedAccountDtoId + 1).currency(Currency.USD).build();
    accountController.updateAccount(patchToNonExistentAccount.getId(), patchToNonExistentAccount);
  }

  /* Now we put transactions in the mix as well. */

  @Test
  public void
      whenPostingAValidTransactionBetweenTwoAccounts_thenTheSourceAccountIsDebitedAndTheTargetAccountIsCredited() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    // Post the accounts
    ResponseEntity<EntityModel<AccountDto>> responseEntityAccountOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityAccountTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountDtoOne =
        Objects.requireNonNull(responseEntityAccountOne.getBody()).getContent();
    AccountDto accountDtoTwo =
        Objects.requireNonNull(responseEntityAccountTwo.getBody()).getContent();
    assert accountDtoOne != null && accountDtoTwo != null;
    BigDecimal accountOneBalanceBeforeTransaction = accountDtoOne.getBalance();
    BigDecimal accountTwoBalanceBeforeTransaction = accountDtoTwo.getBalance();

    // Build and Post the transaction
    TransactionDto transactionDto =
        TransactionDto.builder()
            .id(accountDtoOne.getId())
            .sourceAccountId(accountDtoOne.getId())
            .targetAccountId(accountDtoTwo.getId())
            .amount(BigDecimal.ONE)
            .currency(accountDtoTwo.getCurrency())
            .build();
    transactionController.postTransaction(transactionDto);

    // Get the exchange rate between the source and target account currencies:
    ResponseEntity<Map<CurrencyPair, BigDecimal>> responseEntityForExchangeRate =
        currencyLedgerController.getCurrencyExchangeRate(
            accountDtoOne.getCurrency(), accountDtoTwo.getCurrency(), null, null);
    BigDecimal exchangeRate =
        Objects.requireNonNull(responseEntityForExchangeRate.getBody())
            .get(CurrencyPair.of(accountDtoOne.getCurrency(), accountDtoTwo.getCurrency()));

    // Get the accounts:
    responseEntityAccountOne = accountController.getAccount(accountDtoOne.getId());
    responseEntityAccountTwo = accountController.getAccount(accountDtoTwo.getId());
    accountDtoOne = Objects.requireNonNull(responseEntityAccountOne.getBody()).getContent();
    accountDtoTwo = Objects.requireNonNull(responseEntityAccountTwo.getBody()).getContent();
    assert accountDtoOne != null && accountDtoTwo != null;
    BigDecimal accountOneBalanceAfterTransaction = accountDtoOne.getBalance();
    BigDecimal accountTwoBalanceAfterTransaction = accountDtoTwo.getBalance();

    // Make sure the source account has been appropriately debited and the target account
    // appropriately debited.
    assertEquals(
        accountOneBalanceAfterTransaction,
        accountOneBalanceBeforeTransaction.subtract(
            exchangeRate.multiply(transactionDto.getAmount())));
    assertEquals(
        accountTwoBalanceAfterTransaction,
        accountTwoBalanceBeforeTransaction.add(transactionDto.getAmount()));
  }

  @Test
  public void
      whenPostingAValidTransactionBetweenTwoAccounts_thenWeCanFindTheTransactionInMultipleWays() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    ResponseEntity<EntityModel<AccountDto>> responseEntityOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountDtoOne = Objects.requireNonNull(responseEntityOne.getBody()).getContent();
    AccountDto accountDtoTwo = Objects.requireNonNull(responseEntityTwo.getBody()).getContent();
    assert accountDtoOne != null && accountDtoTwo != null;
    TransactionDto transactionDto =
        TransactionDto.builder()
            .id(accountDtoOne.getId())
            .sourceAccountId(accountDtoOne.getId())
            .targetAccountId(accountDtoTwo.getId())
            .amount(
                new BigDecimal(
                    "1.00")) // BigDecimal.One here leads to a false failure of the test (1 vs 1.00)
            .currency(accountDtoTwo.getCurrency())
            .build();
    transactionController.postTransaction(transactionDto);
    assertEquals(
        ResponseEntity.ok(transactionModelAssembler.toModel(transactionDto)),
        transactionController.getTransaction(transactionDto.getId()));
    List<ResponseEntity<CollectionModel<EntityModel<TransactionDto>>>> responseEntities =
        Arrays.asList(
            transactionController.getAllTransactions(
                Collections.emptyMap(),
                Integer.parseInt(DEFAULT_PAGE_IDX),
                Integer.parseInt(DEFAULT_PAGE_SIZE),
                DEFAULT_SORT_BY_FIELD,
                SortOrder.ASC),
            transactionController.getAllTransactions(
                Map.of(SOURCE_ACCOUNT_ID, accountDtoOne.getId().toString()),
                Integer.parseInt(DEFAULT_PAGE_IDX),
                Integer.parseInt(DEFAULT_PAGE_SIZE),
                DEFAULT_SORT_BY_FIELD,
                SortOrder.ASC),
            transactionController.getAllTransactions(
                Map.of(TARGET_ACCOUNT_ID, accountDtoTwo.getId().toString()),
                Integer.parseInt(DEFAULT_PAGE_IDX),
                Integer.parseInt(DEFAULT_PAGE_SIZE),
                DEFAULT_SORT_BY_FIELD,
                SortOrder.ASC),
            transactionController.getAllTransactions(
                Map.of(
                    SOURCE_ACCOUNT_ID,
                    accountDtoOne.getId().toString(),
                    TARGET_ACCOUNT_ID,
                    accountDtoTwo.getId().toString()),
                Integer.parseInt(DEFAULT_PAGE_IDX),
                Integer.parseInt(DEFAULT_PAGE_SIZE),
                DEFAULT_SORT_BY_FIELD,
                SortOrder.ASC));
    assertTrue(
        responseEntities.stream()
            .allMatch(
                responseEntity ->
                    Objects.requireNonNull(responseEntity.getBody())
                        .getContent()
                        .contains(transactionModelAssembler.toModel(transactionDto))));
  }

  @Test
  public void testPaginationAndSorting_ForGetAllTransactions() {
    when(currencyLedger.getCurrencyExchangeRates()).thenReturn(TEST_EXCHANGE_RATES);
    TEST_ACCOUNT_DTOS.forEach(accountController::postAccount);
    TEST_VALID_TRANSACTION_DTOS.forEach(transactionController::postTransaction);

    // There are 64 transactions total.

    // First, test that we return all of the records if we want to, for all sorting fields and for
    // both directions.
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(64)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Now do the same, but for 4 pages of 16 records each.
    PaginationTester.builder()
        .totalPages(4)
        .pageSize(16)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Now, do this for 6 pages of 12 records each (except for the last one, which should have 4
    // since 64 = 5 * 12 + 4).
    PaginationTester.builder()
        .totalPages(6)
        .pageSize(12)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Now 16 pages of 4 records each
    PaginationTester.builder()
        .totalPages(16)
        .pageSize(4)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Finally, 64 pages of 1 record each
    PaginationTester.builder()
        .totalPages(64)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(Collections.emptyMap())
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);
  }

  private void testSortedAndPaginatedAggregateGetOfTransactions(
          AggregateGetQueryParams aggregateGetQueryParams, Integer expectedNumberOfRecords) {
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    Map<String, String> transactionParams = aggregateGetQueryParams.getTransactionQueryParams();
    List<TransactionDto> expectedTransactionDtos;
    if (transactionParams.containsKey(SOURCE_ACCOUNT_ID)
        && transactionParams.containsKey(TARGET_ACCOUNT_ID)) {
      expectedTransactionDtos =
          TEST_VALID_TRANSACTION_DTOS.stream()
              .filter(
                  transactionDto ->
                      transactionDto
                              .getSourceAccountId()
                              .equals(Long.valueOf(transactionParams.get(SOURCE_ACCOUNT_ID)))
                          && transactionDto
                              .getTargetAccountId()
                              .equals(Long.valueOf(transactionParams.get(TARGET_ACCOUNT_ID))))
              .sorted(
                  (t1, t2) ->
                      compareFieldsInGivenOrder(
                          t1.getClass(), t2.getClass(), sortByField, sortOrder))
              .collect(Collectors.toList())
              .subList(page * pageSize, pageSize * (page + 1));
    } else if (transactionParams.containsKey(SOURCE_ACCOUNT_ID)) {
      expectedTransactionDtos =
          TEST_VALID_TRANSACTION_DTOS.stream()
              .filter(
                  transactionDto ->
                      transactionDto
                          .getSourceAccountId()
                          .equals(Long.valueOf(transactionParams.get(SOURCE_ACCOUNT_ID))))
              .sorted(
                  (t1, t2) ->
                      compareFieldsInGivenOrder(
                          t1.getClass(), t2.getClass(), sortByField, sortOrder))
              .collect(Collectors.toList())
              .subList(page * pageSize, pageSize * (page + 1));
    } else if (transactionParams.containsKey(TARGET_ACCOUNT_ID)) {
      expectedTransactionDtos =
          TEST_VALID_TRANSACTION_DTOS.stream()
              .filter(
                  transactionDto ->
                      transactionDto
                          .getTargetAccountId()
                          .equals(Long.valueOf(transactionParams.get(TARGET_ACCOUNT_ID))))
              .sorted(
                  (t1, t2) ->
                      compareFieldsInGivenOrder(
                          t1.getClass(), t2.getClass(), sortByField, sortOrder))
              .collect(Collectors.toList())
              .subList(page * pageSize, pageSize * (page + 1));
    } else {
      expectedTransactionDtos =
          TEST_VALID_TRANSACTION_DTOS.stream()
              .sorted(
                  (t1, t2) ->
                      compareFieldsInGivenOrder(
                          t1.getClass(), t2.getClass(), sortByField, sortOrder))
              .collect(Collectors.toList())
              .subList(page * pageSize, pageSize * (page + 1));
    }
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> responseEntity =
        transactionController.getAllTransactions(
            transactionParams, page, pageSize, sortByField, sortOrder);
    assertEquals(
        ResponseEntity.ok(transactionModelAssembler.toCollectionModel(expectedTransactionDtos)),
        responseEntity);
  }

  @Test
  public void testPaginationAndSorting_ForGetAllTransactionsFromASourceAccount() {
    when(currencyLedger.getCurrencyExchangeRates()).thenReturn(TEST_EXCHANGE_RATES);
    TEST_ACCOUNT_DTOS.forEach(accountController::postAccount);
    TEST_VALID_TRANSACTION_DTOS.forEach(transactionController::postTransaction);

    // There are 4 transactions coming out of account 1.

    // Test a page with all 4
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(4)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Test 2 with 2 each
    PaginationTester.builder()
        .totalPages(2)
        .pageSize(2)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Test 4 with 1 each
    PaginationTester.builder()
        .totalPages(4)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);
  }

  @Test
  public void testPaginationAndSorting_ForGetAllTransactionsToADestinationAccount() {
    when(currencyLedger.getCurrencyExchangeRates()).thenReturn(TEST_EXCHANGE_RATES);
    TEST_ACCOUNT_DTOS.forEach(accountController::postAccount);
    TEST_VALID_TRANSACTION_DTOS.forEach(transactionController::postTransaction);

    // There are 15 transactions going into account 1.

    // First, test a page with all 15.
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(15)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Now, 5 pages with 3 each.
    PaginationTester.builder()
        .totalPages(5)
        .pageSize(3)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Now, 3 pages with 5 each.

    PaginationTester.builder()
        .totalPages(3)
        .pageSize(5)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);

    // Finally, 15 pages with 1 each.

    PaginationTester.builder()
        .totalPages(15)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(Map.of(TARGET_ACCOUNT_ID, Long.toString(1L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);
  }

  @Test
  public void testPaginationAndSorting_ForGetAllTransactionsFromASourceAndToADestinationAccount() {
    when(currencyLedger.getCurrencyExchangeRates()).thenReturn(TEST_EXCHANGE_RATES);
    TEST_ACCOUNT_DTOS.forEach(accountController::postAccount);
    TEST_VALID_TRANSACTION_DTOS.forEach(transactionController::postTransaction);

    // There is only 1 transaction from account 1 to account 2.
    PaginationTester.builder()
        .totalPages(1)
        .pageSize(1)
        .pojoType(TransactionDto.class)
        .accountParams(
            Map.of(SOURCE_ACCOUNT_ID, Long.toString(1L), TARGET_ACCOUNT_ID, Long.toString(2L)))
        .build()
        .runTest(this::testSortedAndPaginatedAggregateGetOfTransactions);
  }

  @Test(expected = AccountNotFoundException.class)
  public void
      whenPostingATransactionFromANonExistentAccount_thenANonExistentAccountExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountDto = Objects.requireNonNull(responseEntity.getBody()).getContent();
    assert accountDto != null;
    transactionController.postTransaction(
        TransactionDto.builder()
            .sourceAccountId(accountDto.getId() + 1)
            .targetAccountId(accountDto.getId())
            .amount(BigDecimal.ONE)
            .currency(accountDto.getCurrency())
            .build());
  }

  @Test(expected = AccountNotFoundException.class)
  public void
      whenPostingATransactionToANonExistentAccount_thenANonExistentAccountExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto accountDto = Objects.requireNonNull(responseEntity.getBody()).getContent();
    assert accountDto != null;
    transactionController.postTransaction(
        TransactionDto.builder()
            .sourceAccountId(accountDto.getId())
            .targetAccountId(accountDto.getId() + 1)
            .amount(BigDecimal.ONE)
            .currency(
                accountDto
                    .getCurrency()) // Currency doesn't matter here; the NonExistentAccountException
            // should be thrown first.
            .build());
  }

  @Test(expected = TransactionNotFoundException.class)
  public void
      whenGettingATransactionThatHasNotBeenPosted_thenATransactionNotFoundExceptionIsThrown() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    ResponseEntity<EntityModel<AccountDto>> responseEntityOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountDtoOne = Objects.requireNonNull(responseEntityOne.getBody()).getContent();
    AccountDto accountDtoTwo = Objects.requireNonNull(responseEntityTwo.getBody()).getContent();
    assert accountDtoOne != null && accountDtoTwo != null;
    ResponseEntity<EntityModel<TransactionDto>> responseEntityThree =
        transactionController.postTransaction(
            TransactionDto.builder()
                .sourceAccountId(accountDtoOne.getId())
                .targetAccountId(accountDtoTwo.getId())
                .amount(BigDecimal.ONE)
                .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
                .build());
    TransactionDto transactionDto =
        Objects.requireNonNull(responseEntityThree.getBody()).getContent();
    assert transactionDto != null;
    transactionController.getTransaction(transactionDto.getId() + 1);
  }

  @Test(expected = SameAccountException.class)
  public void whenPostingATransactionFromAnAccountToItself_thenSameAccountExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto newAccount = Objects.requireNonNull(responseEntity.getBody()).getContent();
    assert newAccount != null;
    transactionController.postTransaction(
        TransactionDto.builder()
            .sourceAccountId(newAccount.getId())
            .targetAccountId(newAccount.getId())
            .amount(BigDecimal.ONE)
            .currency(newAccount.getCurrency())
            .build());
  }

  @Test(expected = InsufficientBalanceException.class)
  public void
      whenPostingATransactionForWhichThereIsAnInsufficientBalanceInSourceAccount_thenInsufficientBalanceExceptionIsThrown() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    // TEST_ACCOUNT_DTO_ONE and TEST_ACCOUNT_DTO_TWO are both over USD, with an exchange rate of
    // 1:1.
    ResponseEntity<EntityModel<AccountDto>> responseEntityOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountDtoOne = Objects.requireNonNull(responseEntityOne.getBody()).getContent();
    AccountDto accountDtoTwo = Objects.requireNonNull(responseEntityTwo.getBody()).getContent();
    assert accountDtoOne != null && accountDtoTwo != null;
    transactionController.postTransaction(
        TransactionDto.builder()
            .sourceAccountId(accountDtoOne.getId())
            .targetAccountId(accountDtoTwo.getId())
            .amount(TEST_ACCOUNT_DTO_ONE.getBalance().add(BigDecimal.TEN))
            .currency(accountDtoTwo.getCurrency())
            .build());
  }

  @Test(expected = TransactionNotFoundException.class)
  public void
      whenPostingATransactionAndThenDeletingIt_thenTransactionCanNoLongerBeFoundByAnyMeans() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountOne =
        Objects.requireNonNull(responseEntityForAccountOne.getBody()).getContent();
    AccountDto accountTwo =
        Objects.requireNonNull(responseEntityForAccountTwo.getBody()).getContent();
    assert accountOne != null && accountTwo != null;
    ResponseEntity<EntityModel<TransactionDto>> responseEntityForTransactionOne =
        transactionController.postTransaction(
            TransactionDto.builder()
                .sourceAccountId(accountOne.getId())
                .targetAccountId(accountTwo.getId())
                .currency(accountTwo.getCurrency())
                .amount(BigDecimal.ONE) // Ok given test entities
                .build());
    TransactionDto transactionDto =
        Objects.requireNonNull(responseEntityForTransactionOne.getBody()).getContent();
    assert transactionDto != null;
    // Let's post two more transactions to generify this test. No need to store returned IDs for
    // those; we only care about the first transaction.
    transactionController.postTransaction(
        TransactionDto.builder()
            .sourceAccountId(accountOne.getId())
            .targetAccountId(accountTwo.getId())
            .amount(BigDecimal.ONE)
            .currency(accountTwo.getCurrency())
            .build());
    transactionController.postTransaction(
        TransactionDto.builder()
            .sourceAccountId(accountTwo.getId())
            .targetAccountId(accountOne.getId())
            .amount(BigDecimal.ONE)
            .currency(accountOne.getCurrency())
            .build());
    assertEquals(
        ResponseEntity.noContent().build(),
        transactionController.deleteTransaction(transactionDto.getId()));
    assertFalse(transactionFoundInGetFromQuery(transactionDto, accountOne.getId()));
    assertFalse(transactionFoundInGetToQuery(transactionDto, accountTwo.getId()));
    assertFalse(
        transactionFoundInGetFromToQuery(transactionDto, accountOne.getId(), accountTwo.getId()));
    assertFalse(transactionFoundInGetAllQuery(transactionDto));
    transactionController.getTransaction(transactionDto.getId()); // This call is expected to throw.
  }

  private boolean transactionFoundInGetFromQuery(
      TransactionDto transactionDto, Long sourceAccountId) {
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> responseEntity =
        transactionController.getAllTransactions(
            Map.of(SOURCE_ACCOUNT_ID, sourceAccountId.toString()),
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC);
    Collection<TransactionDto> transactionsReturned =
        Objects.requireNonNull(responseEntity.getBody()).getContent().stream()
            .map(EntityModel::getContent)
            .toList();
    return transactionsReturned.contains(transactionDto);
  }

  private boolean transactionFoundInGetToQuery(
      TransactionDto transactionDto, Long targetAccountId) {
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> responseEntity =
        transactionController.getAllTransactions(
            Map.of(TARGET_ACCOUNT_ID, targetAccountId.toString()),
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC);
    Collection<TransactionDto> transactionsReturned =
        Objects.requireNonNull(responseEntity.getBody()).getContent().stream()
            .map(EntityModel::getContent)
            .toList();
    return transactionsReturned.contains(transactionDto);
  }

  private boolean transactionFoundInGetFromToQuery(
      TransactionDto transactionDto, Long sourceAccountId, Long targetAccountId) {
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> responseEntity =
        transactionController.getAllTransactions(
            Map.of(
                SOURCE_ACCOUNT_ID,
                sourceAccountId.toString(),
                TARGET_ACCOUNT_ID,
                targetAccountId.toString()),
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC);
    Collection<TransactionDto> transactionsReturned =
        Objects.requireNonNull(responseEntity.getBody()).getContent().stream()
            .map(EntityModel::getContent)
            .toList();
    return transactionsReturned.contains(transactionDto);
  }

  private boolean transactionFoundInGetAllQuery(TransactionDto transactionDto) {
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> responseEntity =
        transactionController.getAllTransactions(
            Collections.emptyMap(),
            Integer.parseInt(DEFAULT_PAGE_IDX),
            Integer.parseInt(DEFAULT_PAGE_SIZE),
            DEFAULT_SORT_BY_FIELD,
            SortOrder.ASC);
    Collection<TransactionDto> transactionsReturned =
        Objects.requireNonNull(responseEntity.getBody()).getContent().stream()
            .map(EntityModel::getContent)
            .toList();
    return transactionsReturned.contains(transactionDto);
  }

  @Test(expected = TransactionNotFoundException.class)
  public void whenPostingATransactionAndDeletingItTwice_thenTransactionNotFoundExceptionIsThrown() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountOne =
        Objects.requireNonNull(responseEntityForAccountOne.getBody()).getContent();
    AccountDto accountTwo =
        Objects.requireNonNull(responseEntityForAccountTwo.getBody()).getContent();
    assert accountOne != null && accountTwo != null;
    ResponseEntity<EntityModel<TransactionDto>> responseEntityForTransactionOne =
        transactionController.postTransaction(
            TransactionDto.builder()
                .sourceAccountId(accountOne.getId())
                .targetAccountId(accountTwo.getId())
                .currency(accountTwo.getCurrency())
                .amount(BigDecimal.ONE) // Ok given test entities
                .build());
    TransactionDto transactionDto =
        Objects.requireNonNull(responseEntityForTransactionOne.getBody()).getContent();
    assert transactionDto != null;
    transactionController.deleteTransaction(transactionDto.getId());
    transactionController.deleteTransaction(transactionDto.getId());
  }

  @Test
  public void whenDeletingAllTransactions_thenNoContentIsReturnedAndNoTransactionsCanBeFound() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    // Post the accounts first ...

    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountOne =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    ResponseEntity<EntityModel<AccountDto>> responseEntityForAccountTwo =
        accountController.postAccount(TEST_ACCOUNT_DTO_TWO);
    AccountDto accountOne =
        Objects.requireNonNull(responseEntityForAccountOne.getBody()).getContent();
    AccountDto accountTwo =
        Objects.requireNonNull(responseEntityForAccountTwo.getBody()).getContent();
    assert accountOne != null && accountTwo != null;

    // Now post three transactions ...

    ResponseEntity<EntityModel<TransactionDto>> responseEntityForTransactionOne =
        transactionController.postTransaction(
            TransactionDto.builder()
                .sourceAccountId(accountOne.getId())
                .targetAccountId(accountTwo.getId())
                .currency(accountTwo.getCurrency())
                .amount(BigDecimal.ONE) // Ok given test entities
                .build());
    TransactionDto transactionDtoOne =
        Objects.requireNonNull(responseEntityForTransactionOne.getBody()).getContent();
    assert transactionDtoOne != null;
    ResponseEntity<EntityModel<TransactionDto>> responseEntityForTransactionTwo =
        transactionController.postTransaction(
            TransactionDto.builder()
                .sourceAccountId(accountOne.getId())
                .targetAccountId(accountTwo.getId())
                .amount(BigDecimal.ONE)
                .currency(accountTwo.getCurrency())
                .build());
    TransactionDto transactionDtoTwo =
        Objects.requireNonNull(responseEntityForTransactionTwo.getBody()).getContent();
    assert transactionDtoTwo != null;
    ResponseEntity<EntityModel<TransactionDto>> responseEntityForTransactionThree =
        transactionController.postTransaction(
            TransactionDto.builder()
                .sourceAccountId(accountTwo.getId())
                .targetAccountId(accountOne.getId())
                .amount(BigDecimal.ONE)
                .currency(accountOne.getCurrency())
                .build());
    TransactionDto transactionDtoThree =
        Objects.requireNonNull(responseEntityForTransactionThree.getBody()).getContent();
    assert transactionDtoThree != null;

    // Now delete all the transactions and make the relevant assertions.

    assertEquals(ResponseEntity.noContent().build(), transactionController.deleteAllTransactions());
    ResponseEntity<CollectionModel<EntityModel<TransactionDto>>>
        responseEntityForGetAllTransactionsQuery =
            transactionController.getAllTransactions(
                Collections.emptyMap(),
                Integer.parseInt(DEFAULT_PAGE_IDX),
                Integer.parseInt(DEFAULT_PAGE_SIZE),
                DEFAULT_SORT_BY_FIELD,
                SortOrder.ASC);
    assertEquals(
        Objects.requireNonNull(responseEntityForGetAllTransactionsQuery.getBody())
            .getContent()
            .size(),
        0);
  }

  /* Exchange rate endpoint tests */

  @Test
  public void whenRequestingAllExchangeRates_thenAllOfThemAreReturned() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    int numberOfCurrencies = Currency.values().length;
    ResponseEntity<Map<CurrencyPair, BigDecimal>> responseEntity =
        currencyLedgerController.getCurrencyExchangeRate(null, null, null, null);
    assertEquals(
        numberOfCurrencies * numberOfCurrencies,
        Objects.requireNonNull(responseEntity.getBody())
            .size()); // n^2 ordered pairs for a set of size n
  }

  @Test
  public void whenRequestingAPageOfTheExchangeRates_thenExactlyThatPageIsReturned() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates(any(Integer.class), any(Integer.class)))
        .thenCallRealMethod();
    // Request pages of size 20.
    final int pageSize = 20;
    List<Integer> pages =
        Stream.iterate(0, n -> n + pageSize).limit(Currency.values().length).toList();
    assertTrue(
        pages.stream()
            .allMatch(
                page ->
                    Objects.requireNonNull(
                                currencyLedgerController
                                    .getCurrencyExchangeRate(null, null, page, pageSize)
                                    .getBody())
                            .size()
                        <= pageSize));
  }

  @Test(expected = OneOfTwoCurrenciesMissingException.class)
  public void
      whenProvidingTheFirstCurrencyButNeglectingTheSecondOne_thenOneOfTwoCurrenciesMissingExceptionIsThrown() {
    currencyLedgerController.getCurrencyExchangeRate(Currency.AED, null, null, null);
  }

  @Test(expected = OneOfTwoCurrenciesMissingException.class)
  public void
      whenProvidingTheSecondCurrencyButNeglectingTheFirstOne_thenOneOfTwoCurrenciesMissingExceptionIsThrown() {
    currencyLedgerController.getCurrencyExchangeRate(null, Currency.AOK, null, null);
  }

  @Test
  public void whenRequestingASpecificExchangeRate_thenTheCorrectOneIsReturned() {
    when(currencyLedger.getRandom()).thenReturn(RANDOM);
    when(currencyLedger.getCurrencyExchangeRates()).thenCallRealMethod();
    // This is a bit of an expensive test, but important for data integrity.
    ResponseEntity<Map<CurrencyPair, BigDecimal>> responseEntity =
        currencyLedgerController.getCurrencyExchangeRate(null, null, null, null);
    Map<CurrencyPair, BigDecimal> exchangeRates = responseEntity.getBody();
    assert exchangeRates != null;
    for (Currency currencyOne : Currency.values()) {
      for (Currency currencyTwo : Currency.values()) {
        CurrencyPair currencyPair = CurrencyPair.of(currencyOne, currencyTwo);
        BigDecimal exchangeRateFromFullLedger = exchangeRates.get(currencyPair);
        ResponseEntity<Map<CurrencyPair, BigDecimal>> responseEntityForSpecificCurrencyPair =
            currencyLedgerController.getCurrencyExchangeRate(currencyOne, currencyTwo, null, null);
        Map<CurrencyPair, BigDecimal> mapForSpecificCurrencyPair =
            responseEntityForSpecificCurrencyPair.getBody();
        assert mapForSpecificCurrencyPair != null && mapForSpecificCurrencyPair.size() == 1;
        BigDecimal exchangeRateThatWeGetForCurrencyPair =
            mapForSpecificCurrencyPair.get(currencyPair);
        assertEquals(exchangeRateFromFullLedger, exchangeRateThatWeGetForCurrencyPair);
      }
    }
  }
}
