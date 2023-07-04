package com.agilebank.service.account;

import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.util.exceptions.AccountNotFoundException;
import com.agilebank.util.exceptions.InvalidBalanceException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;

  @Transactional
  public AccountDto storeAccount(AccountDto accountDto) throws InvalidBalanceException {
    if (accountDto.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidBalanceException(accountDto.getBalance());
    }
    Account savedAccount =
        accountRepository.save(
            Account.builder()
                .balance(accountDto.getBalance())
                .currency(accountDto.getCurrency())
                .build());
    return AccountDto.builder()
        .id(savedAccount.getId())
        .balance(savedAccount.getBalance())
        .currency(savedAccount.getCurrency())
        .build();
  }

  @Transactional(readOnly = true)
  public List<AccountDto> getAllAccounts() {
    return accountRepository.findAll().stream()
        .map(
            account ->
                AccountDto.builder()
                    .id(account.getId())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .build())
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public AccountDto getAccount(Long id) throws AccountNotFoundException {
    return accountRepository
        .findById(id)
        .map(
            account ->
                AccountDto.builder()
                    .id(account.getId())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .build())
        .orElseThrow(() -> new AccountNotFoundException(id));
  }
  
  @Transactional
  public void deleteAccount(Long id) throws AccountNotFoundException{
    Optional<Account> account = accountRepository.findById(id);
    if (account.isPresent()) {
      accountRepository.deleteById(id);
    } else {
      throw new AccountNotFoundException(id);
    }
  }
  
  @Transactional
  public void deleteAllAccounts(){
    accountRepository.deleteAll();
  }
}
