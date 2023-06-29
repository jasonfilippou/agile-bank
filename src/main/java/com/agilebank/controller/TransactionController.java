package com.agilebank.controller;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bankapi")
public class TransactionController {

  private final TransactionService transactionService;

  @Autowired
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping("/newtransaction")
  public ResponseEntity<TransactionDto> postNewTransaction(
      @RequestBody TransactionDto transactionDto) {
    transactionService.storeTransaction(transactionDto);
    return ResponseEntity.ok(transactionDto);
  }
}
