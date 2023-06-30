package com.agilebank.util.logger.persistence;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TransactionRepositoryLogger {
    
    /* save */
    
    @Before("execution(* com.agilebank.persistence.TransactionRepository.save(..))")
    public void beforeSavingNewTransaction(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.TransactionRepository.save(..))")
    public void afterSavingNewTransaction(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }
    
    @AfterThrowing(value = "execution(* com.agilebank.persistence.TransactionRepository.save(..))", throwing = "ex")
    public void afterSavingNewTransactionThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
    
    /* find all */

    @Before("execution(* com.agilebank.persistence.TransactionRepository.findAll(..))")
    public void beforeFindingAllTransactions(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.TransactionRepository.findAll(..))")
    public void afterFindingAllTransactions(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.TransactionRepository.findAll(..))", throwing = "ex")
    public void afterFindingAllTransactionsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
    
    /* find all by source account id */

    @Before("execution(* com.agilebank.persistence.TransactionRepository.findBySourceAccountId(..))")
    public void beforeFindingAllTransactionsBySourceAccountId(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.TransactionRepository.findBySourceAccountId(..))")
    public void afterFindingAllTransactionsBySourceAccountId(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.TransactionRepository.findBySourceAccountId(..))", throwing = "ex")
    public void afterFindingAllTransactionsBySourceAccountIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
    
    /* find all by target account id */

    @Before("execution(* com.agilebank.persistence.TransactionRepository.findByTargetAccountId(..))")
    public void beforeFindingAllTransactionsByTargetAccountId(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.TransactionRepository.findByTargetAccountId(..))")
    public void afterFindingAllTransactionsByTargetAccountId(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.TransactionRepository.findByTargetAccountId(..))", throwing = "ex")
    public void afterFindingAllTransactionsByTargetAccountIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
    
    /* find all by source and by target account ids */

    @Before("execution(* com.agilebank.persistence.TransactionRepository.findBySourceAccountIdAndTargetAccountId(..))")
    public void beforeFindingAllTransactionsBySourceAndTargetAccountId(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.TransactionRepository.findBySourceAccountIdAndTargetAccountId(..))")
    public void afterFindingAllTransactionsBySourceAndTargetAccountId(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.TransactionRepository.findBySourceAccountIdAndTargetAccountId(..))", throwing = "ex")
    public void afterFindingAllTransactionsBySourceAndTargetAccountIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
}
