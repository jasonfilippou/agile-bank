package com.agilebank.integration;

import static com.agilebank.controller.TransactionController.SOURCE_ACCOUNT_ID;
import static com.agilebank.controller.TransactionController.TARGET_ACCOUNT_ID;
import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.agilebank.controller.AccountController;
import com.agilebank.controller.CurrencyLedgerController;
import com.agilebank.controller.TransactionController;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.util.exceptions.*;
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
public class AgileBankIntegrationTest {

  @Autowired private AccountController accountController;

  @Autowired private TransactionController transactionController;

  @Autowired private CurrencyLedgerController currencyLedgerController;

  @Autowired private AccountModelAssembler accountModelAssembler;

  @Autowired private TransactionModelAssembler transactionModelAssembler;

  /* Tests exclusively for accounts first. */

  @Test
  public void whenPostingAValidAccount_accountCanThenBeFound() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    assertEquals(
        ResponseEntity.ok(accountModelAssembler.toModel(TEST_ACCOUNT_DTO_ONE)),
        accountController.getAccount(TEST_ACCOUNT_DTO_ONE.getId()));
  }

  @Test
  public void whenPostingTwoValidAccounts_getAllFindsThemBoth() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_TWO);
    assertEquals(
        ResponseEntity.ok(
            CollectionModel.of(
                Stream.of(TEST_ACCOUNT_DTO_ONE, TEST_ACCOUNT_DTO_TWO)
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
    accountController.postNewAccount(TEST_ACCOUNT_DTO_THREE);
  }

  @Test(expected = NonExistentAccountException.class)
  public void whenGettingAnAccountThatWeHaveNotPosted_thenNonExistentAccountExceptionIsThrown() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_TWO);
    accountController.getAccount(TEST_ACCOUNT_DTO_ONE.getId() + "spam");
  }

  @Test(expected = AccountAlreadyExistsException.class)
  public void whenPostingTheSameAccountTwice_thenAnAccountAlreadyExistsExceptionIsThrown() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
  }

  /* Now we put transactions in the mix as well. */
  @Test
  public void
      whenPostingAValidTransactionBetweenTwoAccounts_thenWeCanFindTheTransactionInMultipleWays() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_TWO);
    transactionController.postNewTransaction(TEST_TRANSACTION_DTO_ONE);
    List<ResponseEntity<CollectionModel<EntityModel<TransactionDto>>>> responseEntities =
        Arrays.asList(
            transactionController.getAllTransactions(Collections.emptyMap()),
            transactionController.getAllTransactions(
                Map.of(SOURCE_ACCOUNT_ID, TEST_ACCOUNT_DTO_ONE.getId())),
            transactionController.getAllTransactions(
                Map.of(TARGET_ACCOUNT_ID, TEST_ACCOUNT_DTO_TWO.getId())),
            transactionController.getAllTransactions(
                Map.of(
                    SOURCE_ACCOUNT_ID,
                    TEST_ACCOUNT_DTO_ONE.getId(),
                    TARGET_ACCOUNT_ID,
                    TEST_ACCOUNT_DTO_TWO.getId())));
    assertTrue(
        responseEntities.stream()
            .allMatch(
                responseEntity ->
                    Objects.requireNonNull(responseEntity.getBody())
                        .getContent()
                        .contains(transactionModelAssembler.toModel(TEST_TRANSACTION_DTO_ONE))));
  }

  @Test(expected = NonExistentAccountException.class)
  public void
      whenPostingATransactionFromANonExistentAccount_thenANonExistentAccountExceptionIsThrown() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_TWO);
    transactionController.postNewTransaction(
        TEST_TRANSACTION_DTO_ONE); // This one goes from acc 1 to acc 2.
  }

  @Test(expected = NonExistentAccountException.class)
  public void
      whenPostingATransactionToANonExistentAccount_thenANonExistentAccountExceptionIsThrown() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    transactionController.postNewTransaction(
        TEST_TRANSACTION_DTO_ONE); // This one goes from acc 1 to acc 2.
  }

  @Test(expected = SameAccountException.class)
  public void whenPostingATransactionFromAnAccountToItself_thenASameAccountExceptionIsThrown() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_TWO);
    transactionController.postNewTransaction(TEST_TRANSACTION_FROM_ACCOUNT_TO_ITSELF);
  }

  @Test(expected = InsufficientBalanceException.class)
  public void
      whenPostingATransactionForWhichThereIsAnInsufficientBalanceInSourceAccount_thenInsufficientBalanceExceptionIsThrown() {
    accountController.postNewAccount(TEST_ACCOUNT_DTO_ONE);
    accountController.postNewAccount(TEST_ACCOUNT_DTO_TWO);
    transactionController.postNewTransaction(
        TEST_TRANSACTION_DTO_TWO); // Specially crafted to be too much for account one, even with
                                   // the real ledger values.
  }
}
