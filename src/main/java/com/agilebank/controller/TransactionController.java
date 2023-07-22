package com.agilebank.controller;

import static com.agilebank.util.Constants.*;

import com.agilebank.model.transaction.TransactionDto;
import com.agilebank.model.transaction.TransactionModelAssembler;
import com.agilebank.service.transaction.TransactionService;
import com.agilebank.util.AggregateGetQueryParams;
import com.agilebank.util.SortOrder;
import com.agilebank.util.exceptions.ExceptionMessageContainer;
import com.agilebank.util.exceptions.InvalidSortByFieldSpecifiedException;
import com.agilebank.util.logicfactory.TransactionParameterLogicFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * {@link RestController} responsible for exposing endpoints related to transactions.
 *
 * @author jason
 * @see AccountController
 */
@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
@Tag(name = "3. Transactions API")
@Validated
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionModelAssembler transactionModelAssembler;

  /**
   * POST endpoint for a new transaction.
   *
   * @param transactionDto A {@link TransactionDto} instance.
   * @return A {@link ResponseEntity} over the HAL-formatted {@link TransactionDto} that was just
   *     stored in the database, and an {@link HttpStatus#OK} status code (if everything goes ok).
   */
  @Operation(summary = "Store a new transaction")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Transaction successfully created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TransactionDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description =
                "Invalid transaction amount (<= 0), insufficient balance in source account, "
                    + "currency different from target account's, or source account same as target account.",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Source or Target account(s) not found",
            content = @Content)
      })
  @PostMapping("/transaction")
  public ResponseEntity<EntityModel<TransactionDto>> postTransaction(
      @RequestBody @Valid TransactionDto transactionDto) {
    return new ResponseEntity<>(
        transactionModelAssembler.toModel(transactionService.storeTransaction(transactionDto)),
        HttpStatus.CREATED);
  }

  /**
   * GET endpoint for a single transaction.
   *
   * @param id The unique ID of a transaction, generated internally by the database.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link TransactionDto} instance,
   *     alongside a {@link HttpStatus#OK} status code if everything goes ok.
   */
  @Operation(summary = "Get transaction by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transaction successfully retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = TransactionDto.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content)
      })
  @GetMapping("/transaction/{id}")
  public ResponseEntity<EntityModel<TransactionDto>> getTransaction(@PathVariable Long id) {
    return ResponseEntity.ok(
        transactionModelAssembler.toModel(transactionService.getTransaction(id)));
  }

  /**
   * Aggregate GET endpoint for all transactions in the database.
   *
   * @param params An optional parameter {@link Map}. The different cases for this parameter are:
   *     <ul>
   *       <li>If it is of form &#123;&quot; sourceAccountId &quot; : &lt; source_account_id
   *           &gt;&#125;, it returns all transactions that originated from the account with id &lt;
   *           source_account_id &gt;.
   *       <li>If it is of form &#123; &quot; targetAccountId &quot; : &lt; target_account_id
   *           &gt;&#125;, it returns all transactions that culminated with the account with id &lt;
   *           target_account_id &gt;.
   *       <li>If it is of form &#123;&quot; sourceAccountId &quot; : &lt; source_account_id &gt;,
   *           &quot; targetAccountId &quot;, &lt; target_account_id &gt;&#125;, it returns all
   *           transactions that originated from the account with id &lt; source_account_id &gt; and
   *           culminated with the account with id &lt; target_account_id &gt;.
   *       <li>If it is {@literal null}, empty or contains keys different from
   *           &quot;sourceAccountId&quot; and &quot;targetAccountId&quot;, it returns all
   *           transactions.
   *     </ul>
   *
   * @return A {@link ResponseEntity} with a HAL-formatted collection of all transactions that
   *     satisfy the parameters.
   */
  @Operation(summary = "Get all transactions")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transactions successfully retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = TransactionDto.class)))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Bad sorting / pagination parameters specified",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Source or destination account(s) not found",
            content = @Content)
      })
  @GetMapping("/transactions")
  public ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> getAllTransactions(
      @Parameter(
              name = "params",
              in = ParameterIn.QUERY,
              required = true,
              schema =
                  @Schema(
                      type = "object",
                      additionalProperties = Schema.AdditionalPropertiesValue.TRUE,
                      ref = "#/components/schemas/ParameterMap"),
              style = ParameterStyle.FORM,
              explode = Explode.TRUE)
          @RequestParam
          Map<String, String> params,
      @RequestParam(name = "page", defaultValue = DEFAULT_PAGE_IDX) @Min(0) Integer page,
      @RequestParam(name = "items_in_page", defaultValue = DEFAULT_PAGE_SIZE) @Min(1)
          Integer pageSize,
      @RequestParam(name = "sort_by_field", defaultValue = DEFAULT_SORT_BY_FIELD) @NonNull @NotBlank
          String sortByField,
      @RequestParam(name = "sort_order", defaultValue = DEFAULT_SORT_ORDER) @NonNull
          SortOrder sortOrder)
      throws InvalidSortByFieldSpecifiedException {
    List<String> transactionFieldNames =
        Arrays.stream(TransactionDto.class.getDeclaredFields()).map(Field::getName).toList();
    if (!transactionFieldNames.contains(sortByField)) {
      throw new InvalidSortByFieldSpecifiedException(sortByField, transactionFieldNames);
    }
    return new TransactionParameterLogicFactory(transactionService, transactionModelAssembler)
        .getResponseEntitySupplier(
            params.containsKey(SOURCE_ACCOUNT_ID),
            params.containsKey(TARGET_ACCOUNT_ID))
            .getResponseEntity(
                    AggregateGetQueryParams.builder()
                    .page(page)
                    .pageSize(pageSize)
                    .sortByField(sortByField)
                    .sortOrder(sortOrder)
                    .transactionQueryParams(params)
                    .build());
  }

  // Exception handler for handling the case of a bad sort order string provided by the user.
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  private ResponseEntity<ExceptionMessageContainer> badSortOrderProvided() {
    return new ResponseEntity<>(
        new ExceptionMessageContainer(
            "Provided an invalid sort order parameter: acceptable values are: "
                + Arrays.toString(SortOrder.values())
                + "."),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Endpoint for DELETE of a specitic transaction.
   *
   * @param id The unique ID of the transaction to delete.
   * @return An instance of {@link ResponseEntity} with the status code {@link
   *     HttpStatus#NO_CONTENT} if everything goes as planned.
   */
  @Operation(summary = "Delete a transaction")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Transaction successfully deleted",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content)
      })
  @DeleteMapping("/transaction/{id}")
  public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
    transactionService.deleteTransaction(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for aggregate DELETE of all transactions.
   *
   * @return An instance of {@link ResponseEntity} with the status code {@link
   *     HttpStatus#NO_CONTENT} if everything goes as planned.
   */
  @Operation(summary = "Delete all transactions")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Transactions successfully deleted",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
      })
  @DeleteMapping("/transaction")
  public ResponseEntity<?> deleteAllTransactions() {
    transactionService.deleteAllTransactions();
    return ResponseEntity.noContent().build();
  }
}
