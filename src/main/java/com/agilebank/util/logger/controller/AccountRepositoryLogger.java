package com.agilebank.util.logger.controller;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

@Component
@Aspect
@Slf4j
public class AccountRepositoryLogger {

    /* Save account */
    @Before("execution(* com.agilebank.persistence.AccountRepository.save(..))")
    public void beforeSavingAccount(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @After("execution(* com.agilebank.persistence.AccountRepository.save(..))")
    public void afterSavingAccount(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.findById(..))", throwing = "ex")
    public void afterSaveThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* find by ID */
    @Before("execution(* com.agilebank.persistence.AccountRepository.findById(..))")
    public void beforeFindingAccountById(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @After("execution(* com.agilebank.persistence.AccountRepository.findById(..))")
    public void afterFindingAccountById(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.findById(..))", throwing = "ex")
    public void afterFindByIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }



}
