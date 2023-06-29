package com.agilebank.service.transaction;

import com.agilebank.model.account.AccountDao;
import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.util.exceptions.InsufficientBalanceException;
import com.agilebank.util.exceptions.InvalidAmountException;
import com.agilebank.util.exceptions.NonExistentAccountException;
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
    }
}
