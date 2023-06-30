package com.agilebank.util.logger.controller;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AccountServiceLogger {

  /* Store account */
  
  @Before("execution(* com.agilebank.service.account.AccountService.storeAccount(..))")
  public void beforeStoringAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.storeAccount(..))")
  public void afterStoringAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(value = "execution(* com.agilebank.service.account.AccountService.storeAccount(..))", throwing = "ex")
  public void afterStoringAccountThrows(JoinPoint jp, Throwable ex){
    log.warn(msg(jp, ex.getClass()));
  }
  
  /* Get account */
  
  @Before("execution(* com.agilebank.service.account.AccountService.getAccount(..))")
  public void beforeGettingAccount(JoinPoint jp){
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.getAccount(..))")
  public void afterGettingAccount(JoinPoint jp){
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(value = "execution(* com.agilebank.service.account.AccountService.getAccount(..))", throwing = "ex")
  public void afterGettingAccountThrows(JoinPoint jp, Throwable ex){
    log.warn(msg(jp, ex.getClass()));
  }
  
  /* Get all accounts */

  @Before("execution(* com.agilebank.service.account.AccountService.getAllAccounts(..))")
  public void beforeGettingAllAccounts(JoinPoint jp){
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.getAllAccounts(..))")
  public void afterGettingAllAccounts(JoinPoint jp){
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(value = "execution(* com.agilebank.service.account.AccountService.getAllAccounts(..))", throwing = "ex")
  public void afterGettingAllAccountsThrows(JoinPoint jp, Throwable ex){
    log.warn(msg(jp, ex.getClass()));
  }
}
