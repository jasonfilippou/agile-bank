package com.agilebank.util.logger.persistence;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

@Component
@Aspect
@Slf4j
public class AccountRepositoryLogger {

    /* save account */

    @Before("execution(* com.agilebank.persistence.AccountRepository.save(..))")
    public void beforeSavingAccount(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.AccountRepository.save(..))")
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

    @AfterReturning("execution(* com.agilebank.persistence.AccountRepository.findById(..))")
    public void afterFindingAccountById(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.findById(..))", throwing = "ex")
    public void afterFindByIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* find all */

    @Before("execution(* com.agilebank.persistence.AccountRepository.findAll(..))")
    public void beforeFindingAllAccounts(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.AccountRepository.findAll(..))")
    public void afterFindingAllAccounts(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.findAll(..))", throwing = "ex")
    public void afterFindAllAccountsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
}
