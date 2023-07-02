package com.agilebank.service.account;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.account.AccountDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.util.exceptions.AccountAlreadyExistsException;
import com.agilebank.util.exceptions.InvalidBalanceException;
import com.agilebank.util.exceptions.NonExistentAccountException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
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
  public AccountDto storeAccount(AccountDto accountDto)
      throws InvalidBalanceException, AccountAlreadyExistsException {
    if (accountDto.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidBalanceException(accountDto.getId(), accountDto.getBalance());
    }
    Optional<AccountDao> accountDao = accountRepository.findById(accountDto.getId());
    if (accountDao.isEmpty()) {
      AccountDao savedAccountDao =
          accountRepository.save(
              new AccountDao(
                  accountDto.getId(),
                  accountDto.getBalance().setScale(2, RoundingMode.HALF_EVEN),
                  accountDto.getCurrency(),
                  new Date()));
      return new AccountDto(
          savedAccountDao.getId(), savedAccountDao.getBalance(), savedAccountDao.getCurrency());
    }
    throw new AccountAlreadyExistsException(accountDto.getId());
  }

  @Transactional(readOnly = true)
  public List<AccountDto> getAllAccounts() {
    return accountRepository.findAll().stream()
        .map(
            accountDao ->
                new AccountDto(
                    accountDao.getId(), accountDao.getBalance(), accountDao.getCurrency()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public AccountDto getAccount(String id) throws NonExistentAccountException {
    return accountRepository
        .findById(id)
        .map(
            accountDao ->
                new AccountDto(
                    accountDao.getId(), accountDao.getBalance(), accountDao.getCurrency()))
        .orElseThrow(() -> new NonExistentAccountException(id));
  }
}
