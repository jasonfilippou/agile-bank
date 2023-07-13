package com.agilebank.unit.service.account;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.UpdateMapper;
import com.agilebank.util.exceptions.AccountNotFoundException;
import com.agilebank.util.exceptions.InvalidBalanceException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceUnitTests {

  @InjectMocks private AccountService accountService;

  @Mock private AccountRepository accountRepository;

  @Mock private UpdateMapper updateMapper;

  @Test
  public void whenRepoSavesANewAccount_thenTheAccountIsReturned() {
    when(accountRepository.save(any(Account.class))).thenReturn(TEST_ACCOUNT_ONE);
    assertEquals(accountService.storeAccount(TEST_ACCOUNT_DTO_ONE), TEST_ACCOUNT_DTO_ONE);
  }

  @Test(expected = InvalidBalanceException.class)
  public void whenAccountHasANonPositiveBalance_thenInvalidBalanceExceptionIsThrown() {
    // Test account 3 is created with a negative balance.
    accountService.storeAccount(TEST_ACCOUNT_DTO_THREE);
  }

  @Test
  public void whenRequestingSpecificAccount_andRepoFindsIt_thenTheAccountIsReturned() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    assertEquals(accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId()), TEST_ACCOUNT_DTO_ONE);
  }

  @Test(expected = AccountNotFoundException.class)
  public void
      whenRequestingSpecificAccount_andRepoDoesNotFindIt_thenThrowNonExistentAccountException() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(Optional.empty());
    accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId());
  }

  @Test
  public void whenRequestingAllAccounts_thenAllAccountsAreReturned() {
    when(accountRepository.findAll())
        .thenReturn(List.of(TEST_ACCOUNT_ONE, TEST_ACCOUNT_TWO, TEST_ACCOUNT_THREE));
    assertTrue(
        CollectionUtils.isEqualCollection(
            accountService.getAllAccounts(),
            List.of(TEST_ACCOUNT_DTO_ONE, TEST_ACCOUNT_DTO_TWO, TEST_ACCOUNT_DTO_THREE)));
  }

  @Test
  public void whenDeletingAnAccountThatIsFoundInRepo_thenOk() {
    when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
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

  @Test
  public void whenReplacingAccount_thenReplacedObjectIsReturned() {
    final Long id = 1L;
    when(accountRepository.findById(id)).thenReturn(Optional.ofNullable(TEST_ACCOUNT_ONE));
    when(accountRepository.save(any(Account.class)))
        .thenReturn(
            Account.builder()
                .id(id)
                .balance(TEST_ACCOUNT_TWO.getBalance())
                .createdAt(TEST_ACCOUNT_TWO.getCreatedAt())
                .currency(TEST_ACCOUNT_TWO.getCurrency())
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

  @Test
  public void whenUpdatingAnExistingAccountWithANewBalance_thenNewAccountInfoIsReturned() {
    AccountDto newAccountInfo =
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(
                TEST_ACCOUNT_DTO_ONE.getBalance().add(BigDecimal.TEN)) // Ensuring balance different
            .build();
    when(accountRepository.findById(newAccountInfo.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    Account patchedAccount =
        Account.builder()
            .id(newAccountInfo.getId())
            .balance(newAccountInfo.getBalance())
            .currency(TEST_ACCOUNT_ONE.getCurrency())
            .createdAt(TEST_ACCOUNT_ONE.getCreatedAt())
            .build();
    when(updateMapper.updateAccountFromDto(newAccountInfo, TEST_ACCOUNT_ONE))
        .thenReturn(patchedAccount);
    when(accountRepository.save(patchedAccount)).thenReturn(patchedAccount);
    assertEquals(
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(newAccountInfo.getBalance())
            .currency(TEST_ACCOUNT_ONE.getCurrency())
            .build(),
        accountService.updateAccount(newAccountInfo.getId(), newAccountInfo));
  }

  @Test
  public void whenUpdatingAnExistingAccountWithANewCurrency_thenNewAccountInfoIsReturned() {
    AccountDto newAccountInfo =
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .currency(Currency.AMD) // TEST_ACCOUNT_ONE has GBP
            .build();
    when(accountRepository.findById(newAccountInfo.getId()))
        .thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    CurrencyLedger currencyLedger = new CurrencyLedger();
    Account patchedAccount =
        Account.builder()
            .id(newAccountInfo.getId())
            .balance(
                currencyLedger.convertAmountToTargetCurrency(
                    TEST_ACCOUNT_ONE.getCurrency(), Currency.AMD, TEST_ACCOUNT_ONE.getBalance()))
            .currency(newAccountInfo.getCurrency())
            .createdAt(TEST_ACCOUNT_ONE.getCreatedAt())
            .build();
    when(updateMapper.updateAccountFromDto(newAccountInfo, TEST_ACCOUNT_ONE))
        .thenReturn(patchedAccount);
    when(accountRepository.save(patchedAccount)).thenReturn(patchedAccount);
    assertEquals(
        AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(
                currencyLedger.convertAmountToTargetCurrency(
                    TEST_ACCOUNT_ONE.getCurrency(), Currency.AMD, TEST_ACCOUNT_ONE.getBalance()))
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
