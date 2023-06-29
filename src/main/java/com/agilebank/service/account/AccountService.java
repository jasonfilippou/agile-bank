package com.agilebank.service.account;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.account.AccountDto;
import com.agilebank.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service

public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public void storeAccount(AccountDto accountDto){
        accountRepository.save(new AccountDao(accountDto.getId(),
                accountDto.getBalance(), accountDto.getCurrency(), new Date()));
    }
}
