package com.agilebank.util.logger.util;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

@Aspect
@Component
@Slf4j
public class JwtTokenUtilLogger {

    /* Get username from token */

    @Before("execution(* com.agilebank.util.JwtTokenUtil.getUsernameFromToken(..))")
    public void beforeGettingUsernameFromToken(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.util.JwtTokenUtil.getUsernameFromToken(..))")
    public void afterGettingUsernameFromToken(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(
            value = ("execution(* com.agilebank.util.JwtTokenUtil.getUsernameFromToken(..))"),
            throwing = "ex")
    public void afterGettingUsernameFromTokenThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }

    /* Getting expiration date from token */

    @Before("execution(* com.agilebank.util.JwtTokenUtil.getExpirationDateFromToken(..))")
    public void beforeGettingExpirationDateFromToken(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.util.JwtTokenUtil.getExpirationDateFromToken(..))")
    public void afterGettingExpirationDateFromToken(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(
            value = ("execution(* com.agilebank.util.JwtTokenUtil.getExpirationDateFromToken(..))"),
            throwing = "ex")
    public void afterGettingExpirationDateFromTokenThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }

    /* Generate the token */

    @Before("execution(* com.agilebank.util.JwtTokenUtil.generateToken(..))")
    public void beforeGeneratingToken(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.util.JwtTokenUtil.generateToken(..))")
    public void afterGeneratingToken(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(
            value = ("execution(* com.agilebank.util.JwtTokenUtil.generateToken(..))"),
            throwing = "ex")
    public void afterGeneratingTokenThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }

    /* Validate the token */

    @Before("execution(* com.agilebank.util.JwtTokenUtil.validateToken(..))")
    public void beforeValidatingToken(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.util.JwtTokenUtil.validateToken(..))")
    public void afterValidatingToken(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(
            value = ("execution(* com.agilebank.util.JwtTokenUtil.validateToken(..))"),
            throwing = "ex")
    public void afterValidatingTokenThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }


}
