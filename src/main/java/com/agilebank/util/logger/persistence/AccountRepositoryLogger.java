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
    public void afterSavingAccountThrows(JoinPoint jp, Throwable ex){
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
    public void afterFindingByIdThrows(JoinPoint jp, Throwable ex){
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
    public void afterFindingAllAccountsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* Delete by ID */

    @Before("execution(* com.agilebank.persistence.AccountRepository.deleteById(..))")
    public void beforeDeletingAnAccountById(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.AccountRepository.deleteById(..))")
    public void afterDeletingAnAccountById(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.deleteById(..))", throwing = "ex")
    public void afterDeletingAnAccountByIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* Delete All */

    @Before("execution(* com.agilebank.persistence.AccountRepository.deleteAll(..))")
    public void beforeDeletingAllAccounts(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.AccountRepository.deleteAll(..))")
    public void afterDeletingAllAccounts(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.deleteAll(..))", throwing = "ex")
    public void afterDeletingAllAccountsThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* Update account */

    @Before("execution(* com.agilebank.persistence.AccountRepository.updateAccountById(..))")
    public void beforeUpdatingAccountById(JoinPoint jp){
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.persistence.AccountRepository.updateAccountById(..))")
    public void afterUpdatingAccountById(JoinPoint jp){
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.persistence.AccountRepository.updateAccountById(..))", throwing = "ex")
    public void afterUpdatingAccountByIdThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
}
