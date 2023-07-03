package com.agilebank.integration;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.agilebank.controller.AccountController;
import com.agilebank.controller.TransactionController;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
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
                linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel())),
        accountController.getAllAccounts());
  }

  @Test
  public void whenGettingAllAccountsWithoutHavingPostedAny_themEmptyListIsReturned() {
    assertEquals(
        ResponseEntity.ok(
            CollectionModel.of(
                Collections.emptyList(),
                linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel())),
        accountController.getAllAccounts());
  }

  @Test(expected = InvalidBalanceException.class)
  public void whenPostingAnAccountWithABadBalance_thenInvalidBalanceExceptionIsThrown() {
    accountController.postAccount(TEST_ACCOUNT_DTO_THREE);
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenGettingAnAccountThatWeHaveNotPosted_thenNonExistentAccountExceptionIsThrown() {
    ResponseEntity<EntityModel<AccountDto>> responseEntity =
        accountController.postAccount(TEST_ACCOUNT_DTO_ONE);
    AccountDto accountDto = Objects.requireNonNull(responseEntity.getBody()).getContent();
    assert accountDto != null;
    accountController.getAccount(accountDto.getId() + 1);
  }

  /* Now we put transactions in the mix as well. */
  @Test
  public void
      whenPostingAValidTransactionBetweenTwoAccounts_thenWeCanFindTheTransactionInMultipleWays() {
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
            transactionController.getAllTransactions(Collections.emptyMap()),
            transactionController.getAllTransactions(
                Map.of(SOURCE_ACCOUNT_ID, accountDtoOne.getId().toString())),
            transactionController.getAllTransactions(
                Map.of(TARGET_ACCOUNT_ID, accountDtoTwo.getId().toString())),
            transactionController.getAllTransactions(
                Map.of(
                    SOURCE_ACCOUNT_ID,
                    accountDtoOne.getId().toString(),
                    TARGET_ACCOUNT_ID,
                    accountDtoTwo.getId().toString())));
    assertTrue(
        responseEntities.stream()
            .allMatch(
                responseEntity ->
                    Objects.requireNonNull(responseEntity.getBody())
                        .getContent()
                        .contains(transactionModelAssembler.toModel(transactionDto))));
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
            .currency(accountDto.getCurrency())
            .build()); // Currency doesn't matter here; the NonExistentAccountException should be
                       // thrown first.
  }

  @Test(expected = TransactionNotFoundException.class)
  public void
      whenGettingATransactionThatHasNotBeenPosted_thenATransactionNotFoundExceptionIsThrown() {
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
  public void whenPostingATransactionFromAnAccountToItself_thenASameAccountExceptionIsThrown() {
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
            .amount(
                new BigDecimal(
                    "19000.80")) // Specially crafted to be too much for account one, even with
                                      // the real ledger values.
            .currency(accountDtoTwo.getCurrency())
            .build());
  }
}
