package com.agilebank.util.logger.service;

import static com.agilebank.util.logger.MethodLoggingMessages.*;

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
public class JwtAuthenticationServiceLogger {

    @Before("execution(* com.agilebank.service.jwtauthentication.JwtAuthenticationService.authenticate(..))")
    public void beforeAuthenticatingUsernameAndPassword(JoinPoint jp) {
        log.info(msgWithoutLastArgument(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.jwtauthentication.JwtAuthenticationService.authenticate(..))")
    public void afterAuthenticatingUsernameAndPassword(JoinPoint jp) {
        log.info(msgWithoutLastArgument(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.jwtauthentication.JwtAuthenticationService.authenticate(..))", throwing = "ex")
    public void afterAuthenticatingUsernameAndPasswordThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }
    
}
