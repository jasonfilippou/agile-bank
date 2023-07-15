package com.agilebank.util.logger.persistence;

import static com.agilebank.util.logger.MethodLoggingMessages.msg;

import com.agilebank.util.logger.Loc;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * AOP-enabled class that logs all calls to {@literal public} methods of repositories annotated with {@link com.agilebank.persistence.CustomRepositoryAnnotation}.
 * Calls to entrance, exit and exception throwing are all logged.
 * By targeting the annotation {@literal @CustomRepositoryAnnotation} on the repositories involved, we only have one logger class for our persistence layer.
 * @author jason
 * @see com.agilebank.util.logger.service.AccountServiceLogger
 * @see com.agilebank.util.logger.controller.AccountControllerLogger
 */
@Component
@Aspect
@Slf4j
public class RepositoryLogger {

  @Before("execution(* (@com.agilebank.persistence.CustomRepositoryAnnotation *..*).*(..))")
  public void beforeCallingAnyRepoMethod(JoinPoint jp) {
    log.info(msg(Loc.BEGIN, jp));
  }

  @AfterReturning("execution(* (@com.agilebank.persistence.CustomRepositoryAnnotation *..*).*(..))")
  public void afterCallingAnyRepoMethod(JoinPoint jp) {
    log.info(msg(Loc.END, jp));
  }

  @AfterThrowing(
      value = ("execution(* (@com.agilebank.persistence.CustomRepositoryAnnotation *..*).*(..))"),
      throwing = "ex")
  public void afterCallingAnyRepoMethodThrows(JoinPoint jp, Throwable ex) {
    log.warn(msg(jp, ex.getClass()));
  }
}
