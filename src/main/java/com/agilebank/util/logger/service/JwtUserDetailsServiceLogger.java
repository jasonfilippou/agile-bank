package com.agilebank.util.logger.service;

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
public class JwtUserDetailsServiceLogger {

    /* Load user details by username */
    @Before("execution(* com.agilebank.service.jwtauthentication.JwtUserDetailsService.loadUserByUsername(..))")
    public void beforeLoadingUserByUsername(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.jwtauthentication.JwtUserDetailsService.loadUserByUsername(..))")
    public void afterLoadingUserByUsername(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.jwtauthentication.JwtUserDetailsService.loadUserByUsername(..))", throwing = "ex")
    public void afterLoadingUserByUsernameThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }


    /* Save user */
    @Before("execution(* com.agilebank.service.jwtauthentication.JwtUserDetailsService.save(..))")
    public void beforeSavingUser(JoinPoint jp) {
        log.info(msg(Loc.BEGIN, jp));
    }

    @AfterReturning("execution(* com.agilebank.service.jwtauthentication.JwtUserDetailsService.save(..))")
    public void afterSavingUser(JoinPoint jp) {
        log.info(msg(Loc.END, jp));
    }

    @AfterThrowing(value = "execution(* com.agilebank.service.jwtauthentication.JwtUserDetailsService.save(..))", throwing = "ex")
    public void afterSavingUserThrows(JoinPoint jp, Throwable ex) {
        log.warn(msg(jp, ex.getClass()));
    }
}
