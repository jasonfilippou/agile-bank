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

@Component
@Aspect
@Slf4j
public class AccountControllerLogger {

  /* POST */
  @Before("execution(* com.agilebank.controller.AccountController.postNewAccount(..))")
  public void beforePostAccount(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* com.agilebank.controller.AccountController.postNewAccount(..))")
  public void afterPostAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(value = "execution(* com.agilebank.controller.AccountController.postNewAccount(..))", throwing = "ex")
  public void afterPostAccountThrows(JoinPoint jp, Throwable ex){
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
  
  @AfterThrowing(value = "execution(* com.agilebank.controller.AccountController.getAccount(..))", throwing = "ex")
  public void afterGetAccountThrows(JoinPoint jp, Throwable ex){
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
  
  @AfterThrowing(value = "execution(* com.agilebank.controller.AccountController.getAllAccounts(..))", throwing = "ex")
  public void afterGetAllAccountsThrows(JoinPoint jp, Throwable ex){
    log.warn(msg(jp, ex.getClass()));
  }
}
