package com.agilebank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.service.account.AccountService;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankapi")
public class AccountController {

  private final AccountService accountService;
  private final AccountModelAssembler accountModelAssembler;

  @Autowired
  public AccountController(
      AccountService accountService, AccountModelAssembler accountModelAssembler) {
    this.accountService = accountService;
    this.accountModelAssembler = accountModelAssembler;
  }

  @PostMapping("/newaccount")
  public ResponseEntity<EntityModel<AccountDto>> postNewAccount(@RequestBody AccountDto accountDto) {
    return ResponseEntity.ok(accountModelAssembler.toModel(accountService.storeAccount(accountDto)));
  }

  @GetMapping("/allaccounts")
  public ResponseEntity<CollectionModel<EntityModel<AccountDto>>> getAllAccounts() {
    return ResponseEntity.ok(CollectionModel.of(
            accountService.getAllAccounts().stream()
                    .map(accountModelAssembler::toModel)
                    .collect(Collectors.toList()), linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel()));
  }

  @GetMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> getAccount(@NonNull @PathVariable String id) {
    return ResponseEntity.ok(accountModelAssembler.toModel(accountService.getAccount(id)));
  }
}
