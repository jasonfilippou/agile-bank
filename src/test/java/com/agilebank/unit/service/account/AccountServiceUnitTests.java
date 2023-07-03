package com.agilebank.unit.service.account;

import static com.agilebank.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.agilebank.model.account.Account;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.service.account.AccountService;
import com.agilebank.util.exceptions.InvalidBalanceException;
import com.agilebank.util.exceptions.NonExistentAccountException;
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
    
    @InjectMocks
    private AccountService accountService;
    
    @Mock private AccountRepository accountRepository;
    
    @Test
    public void whenRepoSavesANewAccount_thenTheAccountIsReturned(){
    when(accountRepository.save(any(Account.class))).thenReturn(TEST_ACCOUNT_ONE);
        assertEquals(accountService.storeAccount(TEST_ACCOUNT_DTO_ONE), TEST_ACCOUNT_DTO_ONE);
    }
    
    @Test(expected = InvalidBalanceException.class)
    public void whenAccountHasANonPositiveBalance_thenInvalidBalanceExceptionIsThrown(){
        // Test account 3 is created with a negative balance.
        accountService.storeAccount(TEST_ACCOUNT_DTO_THREE);
    }
    
    @Test
    public void whenRequestingSpecificAccount_andRepoFindsIt_thenTheAccountIsReturned(){
        when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(Optional.of(TEST_ACCOUNT_ONE));
        assertEquals(accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId()), TEST_ACCOUNT_DTO_ONE);
    }
    
    @Test(expected = NonExistentAccountException.class)
    public void whenRequestingSpecificAccount_andRepoDoesNotFindIt_thenThrowNonExistentAccountException(){
        when(accountRepository.findById(TEST_ACCOUNT_DTO_ONE.getId())).thenReturn(Optional.empty());
        accountService.getAccount(TEST_ACCOUNT_DTO_ONE.getId());
    }
    
    @Test
    public void whenRequestingAllAccounts_thenAllAccountsAreReturned(){
        when(accountRepository.findAll()).thenReturn(List.of(
                TEST_ACCOUNT_ONE, TEST_ACCOUNT_TWO, TEST_ACCOUNT_THREE));
        assertTrue(CollectionUtils.isEqualCollection(accountService.getAllAccounts(), List.of(
                TEST_ACCOUNT_DTO_ONE, TEST_ACCOUNT_DTO_TWO, TEST_ACCOUNT_DTO_THREE)));
    }
}
