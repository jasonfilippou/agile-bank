package com.agilebank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.service.account.AccountService;
import java.util.List;
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
    public AccountController(AccountService accountService, AccountModelAssembler accountModelAssembler){
        this.accountService = accountService;
        this.accountModelAssembler = accountModelAssembler;
    }
    @PostMapping("/newaccount")
    public ResponseEntity<AccountDto> postNewAccount(@RequestBody AccountDto accountDto){
        accountService.storeAccount(accountDto);
        return ResponseEntity.ok(accountDto);
    }

    @GetMapping("/allaccounts")
    public CollectionModel<EntityModel<AccountDto>> getAllAccounts(){
        List<EntityModel<AccountDto>> accounts = accountService.findAll().stream() //
                .map(accountModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(accounts, linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel());
    }

    @GetMapping("/account/{id}")
    public EntityModel<AccountDto> getAccount(@NonNull @PathVariable String id){
        return accountModelAssembler.toModel(accountService.getAccount(id));
    }
}
