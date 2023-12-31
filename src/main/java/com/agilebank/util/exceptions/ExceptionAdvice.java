package com.agilebank.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestControllerAdvice} for all our custom (and one non-custom) exceptions.
 * @author jason
 */
@RestControllerAdvice
public class ExceptionAdvice {

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#BAD_REQUEST}.
   * @param exc The {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#BAD_REQUEST} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    InsufficientBalanceException.class,
    SameAccountException.class,
    InvalidTransactionCurrencyException.class,
    OneOfTwoCurrenciesMissingException.class,
    InvalidSortByFieldSpecifiedException.class,
    MethodArgumentNotValidException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ExceptionMessageContainer> badRequestStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionMessageContainer(exc.getMessage()), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#NOT_FOUND}.
   * @param exc The {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#NOT_FOUND} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class, UsernameNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ExceptionMessageContainer> notFoundStatusMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionMessageContainer(exc.getMessage()), HttpStatus.NOT_FOUND);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status code of {@link HttpStatus#CONFLICT}
   * @param exc he {@link Exception} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#CONFLICT} as the status code.
   *
   */
  @ResponseBody
  @ExceptionHandler({UsernameAlreadyInDatabaseException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  private ResponseEntity<ExceptionMessageContainer> conflictMessage(Exception exc) {
    return new ResponseEntity<>(new ExceptionMessageContainer(exc.getMessage()), HttpStatus.CONFLICT);
  }
}
