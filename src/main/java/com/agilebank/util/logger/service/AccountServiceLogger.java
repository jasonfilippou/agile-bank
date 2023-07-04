package com.agilebank.util.logger.service;

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

  @AfterThrowing(
      value = "execution(* com.agilebank.service.account.AccountService.storeAccount(..))",
      throwing = "ex")
  public void afterStoringAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Get single account */

  @Before("execution(* com.agilebank.service.account.AccountService.getAccount(..))")
  public void beforeGettingAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.getAccount(..))")
  public void afterGettingAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.service.account.AccountService.getAccount(..))",
      throwing = "ex")
  public void afterGettingAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Get all accounts */

  @Before("execution(* com.agilebank.service.account.AccountService.getAllAccounts(..))")
  public void beforeGettingAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.getAllAccounts(..))")
  public void afterGettingAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.service.account.AccountService.getAllAccounts(..))",
      throwing = "ex")
  public void afterGettingAllAccountsThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Delete account by ID */

  @Before("execution(* com.agilebank.service.account.AccountService.deleteAccount(..))")
  public void beforeDeletingAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.deleteAccount(..))")
  public void afterDeletingAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.service.account.AccountService.deleteAccount(..))",
      throwing = "ex")
  public void afterDeletingAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Delete all accounts */

  @Before("execution(* com.agilebank.service.account.AccountService.deleteAllAccounts(..))")
  public void beforeDeletingAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.deleteAllAccounts(..))")
  public void afterDeletingAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.service.account.AccountService.deleteAllAccounts(..))",
      throwing = "ex")
  public void afterDeletingAllAccountsThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Update account */

  @Before("execution(* com.agilebank.service.account.AccountService.updateAccount(..))")
  public void beforeUpdatingAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.service.account.AccountService.updateAccount(..))")
  public void afterUpdatingAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.service.account.AccountService.updateAccount(..))",
      throwing = "ex")
  public void afterUpdatingAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }
}
