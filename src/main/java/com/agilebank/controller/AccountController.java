package com.agilebank.controller;

import com.agilebank.model.account.AccountDto;
import com.agilebank.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bankapi")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }
    @PostMapping("/newaccount")
    public ResponseEntity<AccountDto> postNewAccount(@RequestBody AccountDto accountDto){
        accountService.storeAccount(accountDto);
        return ResponseEntity.ok(accountDto);
    }
}
