package com.agilebank.util.logger.controller;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

@Component
@Aspect
@Slf4j
public class JWTAuthenticationControllerLogger {

    /* Authenticate Endpoint */
    @Before("execution(* com.agilebank.controller.JwtAuthenticationController.createAuthenticationToken(..))")
    public void beforeCreatingAuthenticationToken(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.controller.JwtAuthenticationController.createAuthenticationToken(..))")
    public void afterCreatingAuthenticationToken(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.controller.JwtAuthenticationController.createAuthenticationToken(..))", throwing = "ex")
    public void afterCreatingAuthenticationTokenThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }

    /* Register Endpoint */

    @Before("execution(* com.agilebank.controller.JwtAuthenticationController.registerUser(..))")
    public void beforeRegisteringUser(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.controller.JwtAuthenticationController.registerUser(..))")
    public void afterRegisteringUser(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.controller.JwtAuthenticationController.registerUser(..))", throwing = "ex")
    public void afterRegisteringUserThrows(JoinPoint jp, Throwable ex){
        log.warn(msg(jp, ex.getClass()));
    }
}


