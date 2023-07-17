package com.agilebank.unit.service.account;

import static com.agilebank.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.PaginationTester;
import com.agilebank.util.SortOrder;
import com.agilebank.util.UpdateMapper;
import com.agilebank.util.exceptions.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceUnitTests {

  @InjectMocks private AccountService accountService;

  @Mock private AccountRepository accountRepository;

  @Mock private UpdateMapper updateMapper;
  
  private static final AccountDto TEST_ACCOUNT_DTO_ONE = TEST_ACCOUNT_DTOS.get(0);
  private static final AccountDto TEST_ACCOUNT_DTO_TWO = TEST_ACCOUNT_DTOS.get(1);
  private static final AccountDto TEST_ACCOUNT_DTO_THREE = TEST_ACCOUNT_DTOS.get(2);

  private static final Account TEST_ACCOUNT_ENTITY_ONE = TEST_ACCOUNT_ENTITIES.get(0);
  private static final Account TEST_ACCOUNT_ENTITY_TWO = TEST_ACCOUNT_ENTITIES.get(1);
  private static final Account TEST_ACCOUNT_ENTITY_THREE = TEST_ACCOUNT_ENTITIES.get(2);



  /* Store account tests */
  @Test
  public void whenRepoSavesANewAccount_thenTheAccountIsReturned() {
    when(accountRepository.save(any(Account.class))).thenReturn(TEST_ACCOUNT_ENTITY_ONE);
    assertEquals(accountService.storeAccount(TEST_ACCOUNT_DTO_ONE), TEST_ACCOUNT_DTO_ONE);
  }

  /* Retrieve account by ID tests */
  
  @Test
  public void whenRequestingSpecificAccount_andRepoFindsIt_thenTheAccountIsReturned() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ENTITY_ONE));
    assertEquals(accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId()), TEST_ACCOUNT_DTO_ONE);
  }

  @Test(expected = AccountNotFoundException.class)
  public void
      whenRequestingSpecificAccount_andRepoDoesNotFindIt_thenThrowNonExistentAccountException() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(Optional.empty());
    accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId());
  }

  /* Retrieve all accounts tests */
  
  @Test
  public void whenRequestingAllAccounts_thenAllAccountsAreReturned() {
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

  private void testAggregateGetForGivenParameters(AggregateGetQueryParams aggregateGetQueryParams, Integer expectedRecordsInPage){
    Integer page = aggregateGetQueryParams.getPage();
    Integer pageSize = aggregateGetQueryParams.getPageSize();
    String sortByField = aggregateGetQueryParams.getSortByField();
    SortOrder sortOrder = aggregateGetQueryParams.getSortOrder();
    List<Account> subListOfPage = TEST_ACCOUNT_ENTITIES.stream().sorted((a1, a2) -> compareFieldsInGivenOrder(a1.getClass(), a2.getClass(),
            sortByField, sortOrder)).toList().subList(page * pageSize, pageSize * (page + 1));
    when(accountRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(subListOfPage));
    assertTrue(
            CollectionUtils.isEqualCollection(
                    accountService.getAllAccounts(page, pageSize, sortByField, sortOrder).toList(),
                    subListOfPage.stream().map(account -> 
                            AccountDto.builder()
                                    .id(account.getId())
                                    .balance(account.getBalance())
                                    .currency(account.getCurrency())
                                    .build()).collect(Collectors.toList())));
  }
  
  /* Delete by ID tests */
  
  @Test
  public void whenDeletingAnAccountThatIsFoundInRepo_thenOk() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ENTITY_ONE));
    Throwable expected = null;
    try {
      accountService.deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
    } catch (Throwable thrown) {
      expected = thrown;
    }
    assertNull(expected, "Expected nothing to be thrown by service method.");
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenDeletingAnAccountThatIsNotFoundInRepo_thenAccountNotFoundExceptionIsThrown() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(Optional.empty());
    accountService.deleteAccount(TEST_ACCOUNT_DTO_ONE.getId());
  }

  /* Delete all accounts tests */
  
  @Test
  public void whenDeletingAllAccounts_thenOk() {
    doNothing().when(accountRepository).deleteAll();
    Throwable expected = null;
    try {
      accountService.deleteAllAccounts();
    } catch (Throwable thrown) {
      expected = thrown;
    }
    assertNull(expected, "Expected nothing to be thrown by service method.");
  }

  /* Replace account by ID tests */
  
  @Test
  public void whenReplacingAccount_thenReplacedObjectIsReturned() {
    final Long id = 1L;
    when(accountRepository.findById(id)).thenReturn(Optional.ofNullable(TEST_ACCOUNT_ENTITY_ONE));
    when(accountRepository.save(any(Account.class)))
        .thenReturn(
            Account.builder()
                .id(id)
                .balance(TEST_ACCOUNT_ENTITY_TWO.getBalance())
                .createdAt(TEST_ACCOUNT_ENTITY_TWO.getCreatedAt())
                .currency(TEST_ACCOUNT_ENTITY_TWO.getCurrency())
                .build());
    AccountDto updatedAccountDto =
        AccountDto.builder()
            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
            .build();
    AccountDto accountDto = accountService.replaceAccount(id, updatedAccountDto);
    assertEquals(
        accountDto,
        AccountDto.builder()
            .id(id)
            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
            .build());
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenAttemptingToReplaceANonExistentAccount_thenAccountNotFoundExceptionIsThrown() {
    final Long id = 1L;
    doThrow(new AccountNotFoundException(id)).when(accountRepository).findById(id);
    accountService.replaceAccount(
        id,
        AccountDto.builder()
            .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
            .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
            .build());
  }

  /* Update account by ID tests */
  
  @Test
  public void whenUpdatingAnExistingAccountWithANewBalance_thenNewAccountInfoIsReturned() {
    AccountDto newAccountInfo =
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(
                TEST_ACCOUNT_DTO_ONE.getBalance().add(BigDecimal.TEN)) // Ensuring balance different
            .build();
    when(accountRepository.findById(newAccountInfo.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ENTITY_ONE));
    Account patchedAccount =
        Account.builder()
            .id(newAccountInfo.getId())
            .balance(newAccountInfo.getBalance())
            .currency(TEST_ACCOUNT_ENTITY_ONE.getCurrency())
            .createdAt(TEST_ACCOUNT_ENTITY_ONE.getCreatedAt())
            .build();
    when(updateMapper.updateEntityFromDto(newAccountInfo, TEST_ACCOUNT_ENTITY_ONE))
        .thenReturn(patchedAccount);
    when(accountRepository.save(patchedAccount)).thenReturn(patchedAccount);
    assertEquals(
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(newAccountInfo.getBalance())
            .currency(TEST_ACCOUNT_ENTITY_ONE.getCurrency())
            .build(),
        accountService.updateAccount(newAccountInfo.getId(), newAccountInfo));
  }

  @Test
  public void whenUpdatingAnExistingAccountWithANewCurrency_thenNewAccountInfoIsReturned() {
    AccountDto newAccountInfo =
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .currency(Currency.AMD) // TEST_ACCOUNT_ENTITY_ONE has GBP
            .build();
    when(accountRepository.findById(newAccountInfo.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ENTITY_ONE));
    CurrencyLedger currencyLedger = new CurrencyLedger();
    Account patchedAccount =
        Account.builder()
            .id(newAccountInfo.getId())
            .balance(
                currencyLedger.convertAmountToTargetCurrency(
                    TEST_ACCOUNT_ENTITY_ONE.getCurrency(), Currency.AMD, TEST_ACCOUNT_ENTITY_ONE.getBalance()))
            .currency(newAccountInfo.getCurrency())
            .createdAt(TEST_ACCOUNT_ENTITY_ONE.getCreatedAt())
            .build();
    when(updateMapper.updateEntityFromDto(newAccountInfo, TEST_ACCOUNT_ENTITY_ONE))
        .thenReturn(patchedAccount);
    when(accountRepository.save(patchedAccount)).thenReturn(patchedAccount);
    assertEquals(
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(
                currencyLedger.convertAmountToTargetCurrency(
                    TEST_ACCOUNT_ENTITY_ONE.getCurrency(), Currency.AMD, TEST_ACCOUNT_ENTITY_ONE.getBalance()))
            .currency(newAccountInfo.getCurrency())
            .build(),
        accountService.updateAccount(newAccountInfo.getId(), newAccountInfo));
  }

  @Test(expected = AccountNotFoundException.class)
  public void whenAttemptingToUpdateANonExistentAccount_thenAccountNotFoundExceptionIsThrown() {
    AccountDto accountDto =
        AccountDto.builder().id(0L).currency(Currency.IDR).balance(BigDecimal.ONE).build();
    doThrow(new AccountNotFoundException(accountDto.getId()))
        .when(accountRepository).findById(accountDto.getId());
    accountService.updateAccount(accountDto.getId(), accountDto);
  }
}
