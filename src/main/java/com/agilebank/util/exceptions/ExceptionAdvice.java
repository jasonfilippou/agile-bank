package com.agilebank.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ExceptionAdvice {

  @ResponseBody
  @ExceptionHandler({ HttpMessageNotReadableException.class,
          InsufficientBalanceException.class, InvalidAmountException.class, SameAccountException.class,
          InvalidTransactionCurrencyException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> badRequestStatusMessage(RuntimeException exc){
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }


  @ResponseBody
  @ExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> notFoundStatusMessage(RuntimeException exc){
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_FOUND);
  }
}
