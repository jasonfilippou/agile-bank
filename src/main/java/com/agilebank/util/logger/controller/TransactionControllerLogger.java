package com.agilebank.util.logger.controller;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

@Aspect
@Slf4j
@Component
public class TransactionControllerLogger {

    /* POST transaction */

    @Before("execution(* com.agilebank.controller.TransactionController.postNewTransaction(..))")
    public void beforePostingNewTransaction(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.controller.TransactionController.postNewTransaction(..))")
    public void afterPostingNewTransaction(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.controller.TransactionController.postNewTransaction(..))", throwing = "ex")
    public void afterPostingNewTransactionThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }


    /* GET Transactions between (or all) */

    @Before("execution(* com.agilebank.controller.TransactionController.getAllTransactions(..))")
    public void beforeGettingTransactions(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.controller.TransactionController.getAllTransactions(..))")
    public void afterGettingTransactions(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.controller.TransactionController.getAllTransactions(..))", throwing = "ex")
    public void afterGettingTransactionsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

}
