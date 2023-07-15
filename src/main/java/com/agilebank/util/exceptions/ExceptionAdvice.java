package com.agilebank.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestControllerAdvice} for all our custom (and one non-custom) exceptions.
 * @author jason
 */
@RestControllerAdvice
public class ExceptionAdvice {

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#BAD_REQUEST}.
   * @param exc The {@link RuntimeException} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#BAD_REQUEST} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    InsufficientBalanceException.class,
    SameAccountException.class,
    InvalidTransactionCurrencyException.class,
    OneOfTwoCurrenciesMissingException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> badRequestStatusMessage(RuntimeException exc) {
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Handler for all exceptions that should return an HTTP Status Code of {@link HttpStatus#NOT_FOUND}.
   * @param exc The {@link RuntimeException} thrown by our application.
   * @return A {@link ResponseEntity} with the exception's message as the body and {@link HttpStatus#NOT_FOUND} as the status code.
   */
  @ResponseBody
  @ExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class, UsernameNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> notFoundStatusMessage(RuntimeException exc) {
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_FOUND);
  }
}
