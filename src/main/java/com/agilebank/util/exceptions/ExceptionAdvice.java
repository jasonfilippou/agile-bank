package com.agilebank.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ExceptionAdvice {

  @ResponseBody
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ResponseEntity<String> messageNotReadableException() {
    return new ResponseEntity<>(
        "Bad message format; please check your fields' values and types.",
        HttpStatus.NOT_ACCEPTABLE);
  }

  @ResponseBody
  @ExceptionHandler(InsufficientBalanceException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> insufficientBalanceInSourceAccount(
      InsufficientBalanceException ex) {
    return new ResponseEntity<>(
        "Insufficient balance in source account "
            + ex.getAccountId()
            + " equal to "
            + ex.getAccountBalance(),
        HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(InvalidAmountException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> invalidBalanceRequested(InvalidAmountException ex) {
    return new ResponseEntity<>(
        "Invalid amount requested: " + ex.getAmount() + ".", HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(SameAccountException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> sameSourceAndTargetAccount(SameAccountException ex) {
    return new ResponseEntity<>(
        "Same source and target account " + ex.getAccountId() + " in transaction",
        HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(AccountAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> accountAlreadyExists(AccountAlreadyExistsException ex) {
    return new ResponseEntity<>(
        "Account with ID " + ex.getAccountId() + " already exists.", HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(InvalidTransactionCurrencyException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> transactionCurrencyDifferentFromTargetAccountCurrency
          (InvalidTransactionCurrencyException ex) {
    return new ResponseEntity<>(
        "Transaction in currency " + ex.getTransactionCurrency() + " but target account in currency "  + ex.getTargetAccountCurrency() + ".",
        HttpStatus.BAD_REQUEST);
  }
}
