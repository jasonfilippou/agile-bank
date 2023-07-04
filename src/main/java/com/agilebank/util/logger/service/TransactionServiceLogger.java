package com.agilebank.util.logger.service;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

@Component
@Aspect
@Slf4j
public class TransactionServiceLogger {

    /* Store transaction */
    @Before("execution(* com.agilebank.service.transaction.TransactionService.storeTransaction(..))")
    public void beforeStoringNewTransaction(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.storeTransaction(..))")
    public void afterStoringNewTransaction(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.storeTransaction(..))", throwing = "ex")
    public void afterStoringNewTransactionThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* get specific transaction by ID */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.getTransaction(..))")
    public void beforeGettingTransaction(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.getTransaction(..))")
    public void afterGettingTransaction(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.getTransaction(..))", throwing = "ex")
    public void afterGettingTransactionThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp,ex.getClass()));
    }

    /* get all transactions */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactions(..))")
    public void beforeGettingAllTransactions(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactions(..))")
    public void afterGettingAllTransactions(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.getAllTransactions(..))", throwing = "ex")
    public void afterGettingAllTransactionsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* get all transactions from given source account */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsFrom(..))")
    public void beforeGettingAllTransactionsFrom(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsFrom(..))")
    public void afterGettingAllTransactionsFrom(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsFrom(..))", throwing = "ex")
    public void afterGettingAllTransactionsFromThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* get all transactions to given target account */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsTo(..))")
    public void beforeGettingAllTransactionsTo(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsTo(..))")
    public void afterGettingAllTransactionsTo(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsTo(..))", throwing = "ex")
    public void afterGettingAllTransactionsToThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* get all transactions between given source and given target accounts */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsBetween(..))")
    public void beforeGettingAllTransactionsBetween(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsBetween(..))")
    public void afterGettingAllTransactionsBetween(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.getAllTransactionsBetween(..))", throwing = "ex")
    public void afterGettingAllTransactionsBetweenThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* Delete transaction */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.deleteTransaction(..))")
    public void beforeDeletingTransaction(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.deleteTransaction(..))")
    public void afterDeletingTransaction(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.deleteTransaction(..))", throwing = "ex")
    public void afterDeletingTransactionThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* Delete all transactions */

    @Before("execution(* com.agilebank.service.transaction.TransactionService.deleteAllTransactions(..))")
    public void beforeDeletingAllTransactions(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.transaction.TransactionService.deleteAllTransactions(..))")
    public void afterDeletingAllTransactions(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.transaction.TransactionService.deleteAllTransactions(..))", throwing = "ex")
    public void afterDeletingAllTransactionsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
}
