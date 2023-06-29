package com.agilebank.util.logger.service;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessage.msg;

@Component
@Aspect
@Slf4j
public class AccountServiceLogger {

    /* POST */
    @Before("execution(* com.agilebank.service.account.AccountService.storeAccount(..))")
    public void beforePostRequests(JoinPoint jp) {
        log.info(msg("store account", Loc.BEGIN, jp));
    }

    @After("execution(* com.agilebank.service.account.AccountService.storeAccount(..))")
    public void afterPostRequests(JoinPoint jp) {
        log.info(msg("store account", Loc.END, jp));
    }

}