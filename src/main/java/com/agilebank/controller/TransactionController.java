package com.agilebank.controller;

import static com.agilebank.util.Constants.SOURCE_ACCOUNT_ID;
import static com.agilebank.util.Constants.TARGET_ACCOUNT_ID;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestController} responsible for exposing endpoints related to transactions.
 * @author jason 
 * 
 * @see AccountController
 */
@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Transaction API", version = "v1"))
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionModelAssembler transactionModelAssembler;

  /**
   * POST endpoint for a new transaction.
   * @param transactionDto A {@link TransactionDto} instance.
   * @return A {@link ResponseEntity} over the HAL-formatted {@link TransactionDto} that was just stored in the database,
   * and an {@link HttpStatus#OK} status code (if everything goes ok).
   */
  @PostMapping("/transaction")
  public ResponseEntity<EntityModel<TransactionDto>> postTransaction(
      @RequestBody TransactionDto transactionDto) {
    return new ResponseEntity<>(
        transactionModelAssembler.toModel(transactionService.storeTransaction(transactionDto)),
        HttpStatus.CREATED);
  }

  /**
   * GET endpoint for a single transaction.
   * @param id The unique ID of a transaction, generated internally by the database.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link TransactionDto} instance, alongside a {@link HttpStatus#OK}
   * status code if everything goes ok.
   */
  @GetMapping("/transaction/{id}")
    public ResponseEntity<EntityModel<TransactionDto>> getTransaction(@PathVariable Long id) {
    return ResponseEntity.ok(
        transactionModelAssembler.toModel(transactionService.getTransaction(id)));
  }

  /**
   * Aggregate GET endpoint for all transactions in the database.
   * @param params An optional parameter {@link Map}. The different cases for this parameter are:
   *               <ul>
   *               <li>If it is of form &#123;&quot; sourceAccountId &quot; : &lt; source_account_id &gt;&#125;, it returns all transactions that originated from the account with id
   *               &lt; source_account_id &gt;.</li>
   *               <li>If it is of form &#123; &quot; targetAccountId &quot; : &lt; target_account_id &gt;&#125;, it returns all transactions that culminated with the account with id
   *                &lt; target_account_id &gt;.</li>
   *               <li>If it is of form &#123;&quot; sourceAccountId &quot; : &lt; source_account_id &gt;, &quot; targetAccountId &quot;, &lt; target_account_id &gt;&#125;, it returns all transactions that originated from the account with id
   *                &lt; source_account_id &gt; and culminated with the account with id &lt; target_account_id &gt;.</li>
   *               <li>If it is {@literal null}, empty or contains keys different from &quot;sourceAccountId&quot; and &quot;targetAccountId&quot;, it returns all transactions.</li>
   *               </ul>
   * @return A {@link ResponseEntity} with a HAL-formatted collection of all transactions that satisfy the parameters.
   */
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


  /**
   * Endpoint for DELETE of a specitic transaction.
   * @param id The unique ID of the transaction to delete.
   * @return An instance of {@link ResponseEntity} with the status code {@link HttpStatus#NO_CONTENT} if everything goes as planned.
   */
  @DeleteMapping("/transaction/{id}")
  public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
    transactionService.deleteTransaction(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for aggregate DELETE of all transactions.
   * @return An instance of {@link ResponseEntity} with the status code {@link HttpStatus#NO_CONTENT} if everything goes as planned.
   */
  @DeleteMapping("/transaction")
  public ResponseEntity<?> deleteAllTransactions() {
    transactionService.deleteAllTransactions();
    return ResponseEntity.noContent().build();
  }
}
