package com.agilebank.controller;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankapi")
public class TransactionController {

  public static final String SOURCE_ACCOUNT_ID = "sourceAccountId";
  public static final String TARGET_ACCOUNT_ID = "targetAccountId";

  private final TransactionService transactionService;
  private final TransactionModelAssembler transactionModelAssembler;

  @Autowired
  public TransactionController(
      TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
    this.transactionService = transactionService;
    this.transactionModelAssembler = transactionModelAssembler;
  }

  @PostMapping("/newtransaction")
  public ResponseEntity<EntityModel<TransactionDto>> postNewTransaction(
      @RequestBody TransactionDto transactionDto) {
    return ResponseEntity.ok(transactionModelAssembler.toModel(transactionService.storeTransaction(transactionDto)));
  }

  @GetMapping("/alltransactions")
  public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getAllTransactions(
      @RequestParam Map<String, String> params) {
    if (params.containsKey(SOURCE_ACCOUNT_ID) && params.containsKey(TARGET_ACCOUNT_ID)) {
      return ResponseEntity.ok(transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactionsBetween(params.get(SOURCE_ACCOUNT_ID), params.get(TARGET_ACCOUNT_ID)), params));
    } else if (params.containsKey(SOURCE_ACCOUNT_ID)) {
      return ResponseEntity.ok(transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactionsFrom(params.get(SOURCE_ACCOUNT_ID)), params));
    } else if (params.containsKey(TARGET_ACCOUNT_ID)) {
      return ResponseEntity.ok(transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactionsTo(params.get(TARGET_ACCOUNT_ID)), params));
    } else { // params is null, empty, or contains irrelevant keys; just return all transactions
      return ResponseEntity.ok(transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactions(), params));
    }
  }
}
