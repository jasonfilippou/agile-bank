package com.agilebank.service.account;

import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
import com.agilebank.persistence.AccountRepository;
import com.agilebank.util.SortOrder;
import com.agilebank.util.UpdateMapper;
import com.agilebank.util.exceptions.AccountNotFoundException;

import com.agilebank.util.exceptions.InvalidPaginationParametersSpecifiedException;
import com.agilebank.util.exceptions.InvalidSortByFieldSpecifiedException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for accounts. Supports {@link com.agilebank.controller.AccountController} by providing methods for retrieving,
 * storing, deleting and updating accounts in the DB.
 * 
 * @author jason 
 * 
 * @see com.agilebank.controller.AccountController
 * @see AccountRepository
 */
@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UpdateMapper updateMapper;

  /**
   * Store a new account in the DB.
   * @param accountDto the {@link AccountDto} with the information of the new account to store in the DB.
   * @return A {@link AccountDto} instance with the information of the account that was stored in the DB, if everything went fine.
   */
  @Transactional
  public AccountDto storeAccount(AccountDto accountDto){
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

  /**
   * Get all the accounts that we have stored in the DB.
   *  @param page The page of data we want (zero-indexed).
   * @param pageSize The number of data records in the page.
   * @param sortByField The field of {@link AccountDto} that we want to sort by, in camelCase.
   * @param sortOrder The value of {@link SortOrder} that dictates ascending or descending order.
   * @return A {@link List} over all the accounts in the DB of the specified page and in the given sort order.
   */
  @Transactional(readOnly = true)
  public Page<AccountDto> getAllAccounts(Integer page, Integer pageSize, String sortByField, SortOrder sortOrder) 
  throws InvalidSortByFieldSpecifiedException, InvalidPaginationParametersSpecifiedException{
    if(page < 0 || pageSize < 1){
      throw new InvalidPaginationParametersSpecifiedException(page, pageSize);
    }
    List<String> accountFieldNames = Arrays.stream(AccountDto.class.getDeclaredFields()).
            map(Field::getName).toList();
    if(!accountFieldNames.contains(sortByField)){
      throw new InvalidSortByFieldSpecifiedException(sortByField, accountFieldNames);
    }
    Sort sorter = (sortOrder == SortOrder.ASC ) ? Sort.by(sortByField).ascending() : Sort.by(sortByField).descending();
    List<AccountDto> accounts = accountRepository.findAll(PageRequest.of(page, pageSize, sorter)).stream()
        .map(
            account ->
                AccountDto.builder()
                    .id(account.getId())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .build())
        .toList();
    return new PageImpl<>(accounts);
  }

  /**
   * Retrieve the account with the provided id.
   * @param id A unique {@link Long} identifier associated with the account we want to retrieve.
   * @return A {@link AccountDto} instance corresponding to the account with id {@literal id}.
   * @throws AccountNotFoundException If there is no account with the id {@literal id}.
   */
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

  /**
   * Hard-delete the account with id {@literal id} from the database.
   * @param id The unique id of the account to delete.
   * @throws AccountNotFoundException If the account cannot be found in the database.
   */
  @Transactional
  public void deleteAccount(Long id) throws AccountNotFoundException {
    Optional<Account> account = accountRepository.findById(id);
    if (account.isPresent()) {
      accountRepository.deleteById(id);
    } else {
      throw new AccountNotFoundException(id);
    }
  }

  /**
   * Hard-delete all the accounts from our database, emptying the relevant table.
   */
  @Transactional
  public void deleteAllAccounts() {
    accountRepository.deleteAll();
  }

  /**
   * Replace the account with unique id {@literal id} with the provided {@link AccountDto} ({@literal PUT} semantics).
   * @param id The unique id of the account to replace.
   * @param accountDto The {@link AccountDto} to replace the account with.
   * @return The new {@link AccountDto} stored in the database.
   * @throws AccountNotFoundException if there is no Account with the id {@literal id} in the db.
   */
  @Transactional
  public AccountDto replaceAccount(Long id, AccountDto accountDto) throws AccountNotFoundException {
    Optional<Account> account = accountRepository.findById(id);
    if (account.isPresent()) {
      Account newAccount =
          accountRepository.save(
              // Update the fields appropriately
              Account.builder()
                  .id(id)
                  .createdAt(account.get().getCreatedAt())
                  .currency(accountDto.getCurrency())
                  .balance(accountDto.getBalance())
                  .build());
      return AccountDto.builder()
          .id(newAccount.getId())
          .currency(newAccount.getCurrency())
          .balance(newAccount.getBalance())
          .build();
    }
    throw new AccountNotFoundException(id);
  }

  /**
   * Patch (update) an account.
   * @param id The unique ID of the account.
   * @param accountDto An {@link AccountDto} containing the fields to update the account with.
   * @return The updated {@link AccountDto} describing the new state of the account.
   */
  @Transactional
  public AccountDto updateAccount(Long id, AccountDto accountDto){
    Optional<Account> accountOptional = accountRepository.findById(id);
    if(accountOptional.isPresent()){
      Account account = accountOptional.get();
      account = updateMapper.updateEntityFromDto(accountDto, account);
      Account patchedAccount = accountRepository.save(account);
      return AccountDto.builder()
              .id(patchedAccount.getId())
              .currency(patchedAccount.getCurrency())
              .balance(patchedAccount.getBalance())
              .build();
    }
    throw new AccountNotFoundException(id);
  }
}
