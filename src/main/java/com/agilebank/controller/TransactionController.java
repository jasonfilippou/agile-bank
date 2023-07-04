package com.agilebank.controller;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionModelAssembler transactionModelAssembler;

  @PostMapping("/transaction")
  public ResponseEntity<EntityModel<TransactionDto>> postTransaction(
      @RequestBody TransactionDto transactionDto) {
    return new ResponseEntity<>(
        transactionModelAssembler.toModel(transactionService.storeTransaction(transactionDto)),
        HttpStatus.CREATED);
  }

  @GetMapping("/transaction/{id}")
  public ResponseEntity<EntityModel<TransactionDto>> getTransaction(@PathVariable Long id) {
    return ResponseEntity.ok(
        transactionModelAssembler.toModel(transactionService.getTransaction(id)));
  }

  @GetMapping("/transactions")
  public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getAllTransactions(
      @RequestParam Map<String, String> params) {
    if (params.containsKey(SOURCE_ACCOUNT_ID) && params.containsKey(TARGET_ACCOUNT_ID)) {
      return ResponseEntity.ok(
          transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactionsBetween(
                  Long.valueOf(params.get(SOURCE_ACCOUNT_ID)),
                  Long.valueOf(params.get(TARGET_ACCOUNT_ID))),
              params));
    } else if (params.containsKey(SOURCE_ACCOUNT_ID)) {
      return ResponseEntity.ok(
          transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactionsFrom(
                  Long.valueOf(params.get(SOURCE_ACCOUNT_ID))),
              params));
    } else if (params.containsKey(TARGET_ACCOUNT_ID)) {
      return ResponseEntity.ok(
          transactionModelAssembler.toCollectionModel(
              transactionService.getAllTransactionsTo(Long.valueOf(params.get(TARGET_ACCOUNT_ID))),
              params));
    }
    // params is null, empty, or contains irrelevant keys; just return all transactions
    return ResponseEntity.ok(
        transactionModelAssembler.toCollectionModel(
            transactionService.getAllTransactions(), params));
  }

  @DeleteMapping("/transaction/{id}")
  public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
    transactionService.deleteTransaction(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/transaction")
  public ResponseEntity<?> deleteAllTransactions() {
    transactionService.deleteAllTransactions();
    return ResponseEntity.noContent().build();
  }
}
