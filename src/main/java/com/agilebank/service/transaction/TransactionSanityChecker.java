package com.agilebank.service.transaction;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionSanityChecker {
    public void checkTransaction(TransactionDto transactionDto, Optional<AccountDao> sourceAccount, Optional<AccountDao> targetAccount){
        if(sourceAccount.isEmpty()){
            throw new NonExistentAccountException(transactionDto.getSourceAccountId().strip());
        }
        if (targetAccount.isEmpty()){
            throw new NonExistentAccountException(transactionDto.getTargetAccountId().strip());
        }
        if(transactionDto.getAmount() <= 0){
            throw new InvalidAmountException(transactionDto.getAmount());
        }
        if (transactionDto.getAmount() > sourceAccount.get().getBalance()){
            throw new InsufficientBalanceException(transactionDto.getSourceAccountId().strip(), sourceAccount.get().getBalance(),
                    transactionDto.getAmount());
        }
        if(sourceAccount.get().getCurrency() != targetAccount.get().getCurrency()){
            throw new DifferentCurrenciesException(sourceAccount.get().getCurrency(), targetAccount.get().getCurrency());
        }
        if(transactionDto.getCurrency() != sourceAccount.get().getCurrency()){
            throw new InvalidTransactionCurrencyException(transactionDto.getCurrency());
        }
    }
}
