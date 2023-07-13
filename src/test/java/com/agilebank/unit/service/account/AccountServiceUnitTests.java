package com.agilebank.unit.service.account;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
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
  public void whenUpdatingAnExistingAccountWithANewBalance_thenNewAccountInfoIsReturned(){
    AccountDto newAccountInfo = AccountDto.builder()
            .id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(TEST_ACCOUNT_DTO_ONE.getBalance().add(BigDecimal.TEN)) // Ensuring balance different
            .build();
    when(accountRepository.findById(newAccountInfo.getId())).thenReturn(Optional.of(TEST_ACCOUNT_ONE));
    Account patchedAccount =
        Account.builder()
            .id(newAccountInfo.getId())
            .balance(newAccountInfo.getBalance())
            .currency(TEST_ACCOUNT_ONE.getCurrency())
            .createdAt(TEST_ACCOUNT_ONE.getCreatedAt())
            .build();
    when(updateMapper.updateAccountFromDto(newAccountInfo, TEST_ACCOUNT_ONE)).thenReturn(patchedAccount);
    when(accountRepository.save(patchedAccount)).thenReturn(patchedAccount);
    assertEquals(AccountDto.builder().id(TEST_ACCOUNT_DTO_ONE.getId())
            .balance(newAccountInfo.getBalance())
            .currency(TEST_ACCOUNT_ONE.getCurrency())
            .build(), 
            accountService.patchAccount(newAccountInfo.getId(), newAccountInfo));
  }
}
