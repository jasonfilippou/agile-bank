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

@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final AccountModelAssembler accountModelAssembler;

  @PostMapping("/account")
  public ResponseEntity<EntityModel<AccountDto>> postAccount(@RequestBody AccountDto accountDto) {
    return new ResponseEntity<>(accountModelAssembler.toModel(accountService.storeAccount(accountDto)),
            HttpStatus.CREATED);
  }

  @GetMapping("/account")
  public ResponseEntity<CollectionModel<EntityModel<AccountDto>>> getAllAccounts() {
    return ResponseEntity.ok(accountModelAssembler.toCollectionModel(
            accountService.getAllAccounts()));
  }

  @GetMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> getAccount(@PathVariable Long id) {
    return ResponseEntity.ok(accountModelAssembler.toModel(accountService.getAccount(id)));
  }

  @DeleteMapping("/account/{id}")
  public ResponseEntity<?> deleteAccount(@PathVariable Long id){
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/account")
  public ResponseEntity<?> deleteAllAccounts(){
    accountService.deleteAllAccounts();
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> updateAccount(@PathVariable Long id, @RequestBody AccountDto account){
    return ResponseEntity.ok(accountModelAssembler.toModel(accountService.updateAccount(id, account)));
  }
}
