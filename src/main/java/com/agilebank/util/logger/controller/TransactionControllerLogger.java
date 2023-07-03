package com.agilebank.util.logger.controller;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class TransactionControllerLogger {

  /* POST transaction */

  @Before("execution(* com.agilebank.controller.TransactionController.postTransaction(..))")
  public void beforePostingNewTransaction(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.TransactionController.postTransaction(..))")
  public void afterPostingNewTransaction(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.TransactionController.postTransaction(..))",
      throwing = "ex")
  public void afterPostingNewTransactionThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* GET single Transaction by ID */

  @Before("execution(* com.agilebank.controller.TransactionController.getTransaction(..))")
  public void beforeGettingTransaction(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.TransactionController.getTransaction(..))")
  public void afterGettingTransaction(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.TransactionController.getTransaction(..))",
      throwing = "ex")
  public void afterGettingTransactionThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* GET Transactions between (or all) */

  @Before("execution(* com.agilebank.controller.TransactionController.getAllTransactions(..))")
  public void beforeGettingTransactions(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning(
      "execution(* com.agilebank.controller.TransactionController.getAllTransactions(..))")
  public void afterGettingTransactions(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.TransactionController.getAllTransactions(..))",
      throwing = "ex")
  public void afterGettingTransactionsThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }
}
