package com.agilebank.service.account;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.account.AccountDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.util.exceptions.AccountAlreadyExistsException;
import com.agilebank.util.exceptions.NonExistentAccountException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public void storeAccount(AccountDto accountDto) throws AccountAlreadyExistsException{
    Optional<AccountDao> accountDao = accountRepository.findById(accountDto.getId());
    if (accountDao.isEmpty()) {
      accountRepository.save(
          new AccountDao(
              accountDto.getId(), accountDto.getBalance(), accountDto.getCurrency(), new Date()));
    } else {
      throw new AccountAlreadyExistsException(accountDto.getId());
    }
  }

  public List<AccountDto> getAllAccounts() {
    return accountRepository.findAll().stream()
        .map(
            accountDao ->
                new AccountDto(
                    accountDao.getId(), accountDao.getBalance(), accountDao.getCurrency()))
        .collect(Collectors.toList());
  }

  public AccountDto getAccount(String id) {
    return accountRepository
        .findById(id)
        .map(
            accountDao ->
                new AccountDto(
                    accountDao.getId(), accountDao.getBalance(), accountDao.getCurrency()))
        .orElseThrow(() -> new NonExistentAccountException(id));
  }
}
