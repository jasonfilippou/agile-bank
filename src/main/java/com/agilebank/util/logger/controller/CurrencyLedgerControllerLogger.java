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

@Aspect
@Component
@Slf4j
public class CurrencyLedgerControllerLogger {
  @Before(
      "execution(* com.agilebank.controller.CurrencyLedgerController.getCurrencyExchangeRate(..))")
  public void beforeGettingExchangeRate(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning(
      "execution(* com.agilebank.controller.CurrencyLedgerController.getCurrencyExchangeRate(..))")
  public void afterGettingExchangeRate(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value =
          "execution(* com.agilebank.controller.CurrencyLedgerController.getCurrencyExchangeRate(..))",
      throwing = "ex")
  public void afterGettingExchangeRateThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }
}
