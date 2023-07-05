package com.agilebank.util.logger.controller;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * AOP-enabled class that logs all calls to {@literal public} methods of {@link com.agilebank.controller.AccountController}.
 * Calls to entrance, exit and exception throwing are all logged.
 * @author jason 
 * @see com.agilebank.util.logger.service.AccountServiceLogger
 * @see com.agilebank.util.logger.persistence.AccountRepositoryLogger
 */
@Component
@Aspect
@Slf4j
public class AccountControllerLogger {

  /* POST */
  @Before("execution(* com.agilebank.controller.AccountController.postAccount(..))")
  public void beforePostAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.postAccount(..))")
  public void afterPostAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.AccountController.postAccount(..))",
      throwing = "ex")
  public void afterPostAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* GET account */

  @Before("execution(* com.agilebank.controller.AccountController.getAccount(..))")
  public void beforeGetAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.getAccount(..))")
  public void afterGetAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.AccountController.getAccount(..))",
      throwing = "ex")
  public void afterGetAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* GET ALL accounts */

  @Before("execution(* com.agilebank.controller.AccountController.getAllAccounts(..))")
  public void beforeGetAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.getAllAccounts(..))")
  public void afterGetAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.AccountController.getAllAccounts(..))",
      throwing = "ex")
  public void afterGetAllAccountsThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Delete account */

  @Before("execution(* com.agilebank.controller.AccountController.deleteAccount(..))")
  public void beforeDeleteAccountById(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.deleteAccount(..))")
  public void afterDeleteAccountById(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.AccountController.deleteAccount(..))",
      throwing = "ex")
  public void afterDeleteAccountByIdThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Delete all accounts */

  @Before("execution(* com.agilebank.controller.AccountController.deleteAllAccounts(..))")
  public void beforeDeleteAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.deleteAllAccounts(..))")
  public void afterDeleteAllAccounts(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.AccountController.deleteAllAccounts(..))",
      throwing = "ex")
  public void afterDeleteAllAccountsThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }

  /* Replace account */

  @Before("execution(* com.agilebank.controller.AccountController.replaceAccount(..))")
  public void beforeReplacingAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.replaceAccount(..))")
  public void afterReplacingAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = "execution(* com.agilebank.controller.AccountController.replaceAccount(..))",
      throwing = "ex")
  public void afterReplacingAccountThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }
}
