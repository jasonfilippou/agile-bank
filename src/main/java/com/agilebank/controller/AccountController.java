package com.agilebank.controller;

import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestController} for {@link AccountDto} instances. Offers endpoints for POST, GET, DELETE and PUT REST verbs.
 *
 * @author jason
 * @see TransactionController
 * @see CurrencyLedgerController
 * @see AccountDto
 */
@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final AccountModelAssembler accountModelAssembler;

  /**
   * Endpoint for POST of a single account entity.
   * @param accountDto The {@link AccountDto} entity to POST to the API.
   * @return The {@link AccountDto} entity describing the entity that was stored in the DB.
   */
  @PostMapping("/account")
  public ResponseEntity<EntityModel<AccountDto>> postAccount(@RequestBody AccountDto accountDto) {
    return new ResponseEntity<>(
        accountModelAssembler.toModel(accountService.storeAccount(accountDto)), HttpStatus.CREATED);
  }

  /**
   * Endpoint for aggregate GET of all accounts.
   * @return A {@link ResponseEntity} over a HAL-formatted collection of all accounts with a status of {@link HttpStatus#OK} if everything went well.
   */
  @GetMapping("/account")
  public ResponseEntity<CollectionModel<EntityModel<AccountDto>>> getAllAccounts() {
    return ResponseEntity.ok(
        accountModelAssembler.toCollectionModel(accountService.getAllAccounts()));
  }

  /**
   * Endpoint for GET of a specific account.
   * @param id The unique ID of the account.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link EntityModel} of a specific account.
   */
  @GetMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> getAccount(@PathVariable Long id) {
    return ResponseEntity.ok(accountModelAssembler.toModel(accountService.getAccount(id)));
  }

  /**
   * Endpoint for DELETE of a specific account.
   * @param id The unique ID of the account to delete.
   * @return A {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} if everything went well.
   */
  @DeleteMapping("/account/{id}")
  public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for aggregate DELETE of all accounts.
   * @return A {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} if everything went well.
   */
  @DeleteMapping("/account")
  public ResponseEntity<?> deleteAllAccounts() {
    accountService.deleteAllAccounts();
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for PUT of a specific account.
   * @param id The unique ID of the account to replace.
   * @param account The {@link AccountDto} to replace the existing account with.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link EntityModel} with the new account data.
   */
  @PutMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> replaceAccount(
      @PathVariable Long id, @RequestBody AccountDto account) {
    return ResponseEntity.ok(
        accountModelAssembler.toModel(accountService.replaceAccount(id, account)));
  }
}
