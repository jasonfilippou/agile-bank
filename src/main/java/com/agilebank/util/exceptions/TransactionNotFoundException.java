package com.agilebank.util.exceptions;

import lombok.Getter;

/**
 * A {@link RuntimeException} thrown when a transaction with a given ID is not found in the database.
 * @author jason
 */
@Getter
public class TransactionNotFoundException extends RuntimeException {

  private final Long id;

  public TransactionNotFoundException(Long id) {
    super("Could not find transaction with id: " + id + ".");
    this.id = id;
  }
}
