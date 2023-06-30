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
  public void afterSuccessfullyStoringAccount(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(value = "execution(* com.agilebank.service.account.AccountService.storeAccount(..))", throwing = "ex")
  public void afterThrowing(JoinPoint jp, Throwable ex){
    log.warn(msg(jp, ex.getClass()));
  }


}
