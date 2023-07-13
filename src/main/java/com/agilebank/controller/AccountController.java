package com.agilebank.controller;

import com.agilebank.model.account.AccountDto;
import com.agilebank.model.account.AccountModelAssembler;
import com.agilebank.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestController} for {@link AccountDto} instances. Offers endpoints for POST, GET, DELETE
 * and PUT REST verbs.
 *
 * @author jason
 * @see TransactionController
 * @see CurrencyLedgerController
 * @see AccountDto
 */
@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
@Tag(name = "2. Accounts API")
public class AccountController {

  private final AccountService accountService;
  private final AccountModelAssembler accountModelAssembler;

  /**
   * Endpoint for POST of a single account entity.
   *
   * @param accountDto The {@link AccountDto} entity to POST to the API.
   * @return The {@link AccountDto} entity describing the entity that was stored in the DB.
   */
  @Operation(summary = "Store a new account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "New account successfully stored",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountDto.class))
            }),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user", content = @Content)
      })
  @PostMapping("/account")
  public ResponseEntity<EntityModel<AccountDto>> postAccount(@RequestBody AccountDto accountDto) {
    return new ResponseEntity<>(
        accountModelAssembler.toModel(accountService.storeAccount(accountDto)), HttpStatus.CREATED);
  }

  /**
   * Endpoint for aggregate GET of all accounts.
   *
   * @return A {@link ResponseEntity} over a HAL-formatted collection of all accounts with a status
   *     of {@link HttpStatus#OK} if everything went well.
   */
  @Operation(summary = "Get all accounts")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "All accounts successfully retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = AccountDto.class)))
            }), // TODO: Fix this
        @ApiResponse(responseCode = "401", description = "Unauthenticated user", content = @Content)
      })
  @GetMapping("/account")
  public ResponseEntity<CollectionModel<EntityModel<AccountDto>>> getAllAccounts() {
    return ResponseEntity.ok(
        accountModelAssembler.toCollectionModel(accountService.getAllAccounts()));
  }

  /**
   * Endpoint for GET of a specific account.
   *
   * @param id The unique ID of the account.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link EntityModel} of a specific
   *     account.
   */
  @Operation(summary = "Get account by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account successfully retrieved",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AccountDto.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
      })
  @GetMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> getAccount(@PathVariable Long id) {
    return ResponseEntity.ok(accountModelAssembler.toModel(accountService.getAccount(id)));
  }

  /**
   * Endpoint for DELETE of a specific account.
   *
   * @param id The unique ID of the account to delete.
   * @return A {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} if everything went well.
   */
  @Operation(summary = "Delete account by ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Account successfully deleted",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
      })
  @DeleteMapping("/account/{id}")
  public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for aggregate DELETE of all accounts.
   *
   * @return A {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} if everything went well.
   */
  @Operation(summary = "Delete all accounts")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Accounts successfully deleted",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user", content = @Content)
      })
  @DeleteMapping("/account")
  public ResponseEntity<?> deleteAllAccounts() {
    accountService.deleteAllAccounts();
    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for PUT of a specific account.
   *
   * @param id The unique ID of the account to replace.
   * @param accountDto The {@link AccountDto} to replace the existing account with.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link EntityModel} with the new account
   *     data.
   */
  @Operation(summary = "Replace an account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account successfully replaced",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated user",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
      })
  @PutMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> replaceAccount(
      @PathVariable Long id, @RequestBody AccountDto accountDto) {
    return ResponseEntity.ok(
        accountModelAssembler.toModel(accountService.replaceAccount(id, accountDto)));
  }

  /**
   * Endpoint for PATCH of a specific account.
   *
   * @param id The unique ID of the account to update.
   * @param accountDto The {@link AccountDto} to replace certain fields of the existing account with. Any {@link null}
   *                   or missing fields are avoided; if you wish to persist {@literal null} fields, please use the PUT
   *                   endpoint instead.
   * @return A {@link ResponseEntity} over a HAL-formatted {@link EntityModel} with the new account data.
   */
  @Operation(summary = "Replace an account")
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Account successfully updated",
                          content = @Content),
                  @ApiResponse(
                          responseCode = "401",
                          description = "Unauthenticated user",
                          content = @Content),
                  @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
          })
  @PatchMapping("/account/{id}")
  public ResponseEntity<EntityModel<AccountDto>> updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto){
    return ResponseEntity.ok(
            accountModelAssembler.toModel(accountService.updateAccount(id, accountDto)));
  }
}
