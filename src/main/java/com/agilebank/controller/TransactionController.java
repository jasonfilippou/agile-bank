package com.agilebank.controller;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankapi")
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionModelAssembler transactionModelAssembler;

  @Autowired
  public TransactionController(TransactionService transactionService, TransactionModelAssembler transactionModelAssembler) {
    this.transactionService = transactionService;
    this.transactionModelAssembler = transactionModelAssembler;
  }

  @PostMapping("/newtransaction")
  public ResponseEntity<EntityModel<TransactionDto>> postNewTransaction(
      @RequestBody TransactionDto transactionDto) {
    transactionService.storeTransaction(transactionDto);
    return ResponseEntity.ok(transactionModelAssembler.toModel(transactionDto));
  }
  
  @GetMapping("/alltransactions")
  public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getAllTransactions(@RequestParam Map<String, String> params){
    if(params.containsKey("sourceAccountId") && params.containsKey("targetAccountId")){
      return ResponseEntity.ok(CollectionModel.of(transactionService.getAllTransactionsBetween(params.get("sourceAccountId"), 
              params.get("targetAccountId")).stream().map(transactionModelAssembler::toModel).collect(Collectors.toList())));
    } else if(params.containsKey("sourceAccountId")) {
      return ResponseEntity.ok(CollectionModel.of(transactionService.getAllTransactionsFrom(params.get("sourceAccountId"))
              .stream().map(transactionModelAssembler::toModel).collect(Collectors.toList())));
    } else if(params.containsKey("targetAccountId")){
      return ResponseEntity.ok(CollectionModel.of(transactionService.getAllTransactionsTo(params.get("targetAccountId"))
              .stream().map(transactionModelAssembler::toModel).collect(Collectors.toList())));
    } else { // params is null, empty, or contains irrelevant keys; just return all transactions
      return ResponseEntity.ok(CollectionModel.of(transactionService.getAllTransactions().stream()
              .map(transactionModelAssembler::toModel).collect(Collectors.toList())));
    }
  }
}
