package com.agilebank.util;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;
import static com.agilebank.util.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import com.agilebank.controller.TransactionController;
import com.agilebank.model.account.Account;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.transaction.Transaction;
import com.agilebank.model.transaction.TransactionDto;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public final class TestConstants {

  private TestConstants() {}

  /* Account IDs */
  public static final Long TEST_ACCOUNT_ONE_ID = 1L;
  public static final Long TEST_ACCOUNT_TWO_ID = 2L;
  public static final Long TEST_ACCOUNT_THREE_ID = 3L;

  /* Account DTOs */

  public static final AccountDto TEST_ACCOUNT_DTO_ONE =
      AccountDto.builder()
          .id(TEST_ACCOUNT_ONE_ID)
          .balance(new BigDecimal("120.25"))
          .currency(Currency.GBP)
          .build();

  public static final AccountDto TEST_ACCOUNT_DTO_TWO =
          AccountDto.builder()
                  .id(TEST_ACCOUNT_TWO_ID)
                  .balance(new BigDecimal("801.01"))
                  .currency(Currency.IDR)
                  .build();
  public static final AccountDto TEST_ACCOUNT_DTO_THREE =
          AccountDto.builder()
                  .id(TEST_ACCOUNT_THREE_ID)
                  .balance(new BigDecimal("-51.00")) // Invalid value put here for testing.
                  .currency(Currency.USD)
                  .build();

  /* Accounts */

  public static final Account TEST_ACCOUNT_ONE =
          Account.builder()
                  .id(TEST_ACCOUNT_DTO_ONE.getId())
                  .balance(TEST_ACCOUNT_DTO_ONE.getBalance())
                  .currency(TEST_ACCOUNT_DTO_ONE.getCurrency())
                  .createdAt( new Date(946083999988L))
                  .build();

  public static final Account TEST_ACCOUNT_TWO =
          Account.builder()
                  .id(TEST_ACCOUNT_DTO_TWO.getId())
                  .balance(TEST_ACCOUNT_DTO_TWO.getBalance())
                  .currency(TEST_ACCOUNT_DTO_TWO.getCurrency())
                  .createdAt( new Date(946083999989L))
                  .build();

  public static final Account TEST_ACCOUNT_THREE =
          Account.builder()
                  .id(TEST_ACCOUNT_DTO_THREE.getId())
                  .balance(TEST_ACCOUNT_DTO_THREE.getBalance())
                  .currency(TEST_ACCOUNT_DTO_THREE.getCurrency())
                  .createdAt( new Date(946083999990L))
                  .build();

  /* Constant for relationship of Entity Models for Account DTOs */

  public static final String ALL_ACCOUNTS = "all_accounts";

  /* Entity Models for Account DTOs */

  public static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE =
      EntityModel.of(
          TEST_ACCOUNT_DTO_ONE,
          linkTo(methodOn(AccountController.class).getAccount(TEST_ACCOUNT_DTO_ONE.getId()))
              .withSelfRel(),
          linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS));

  public static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_TWO =
      EntityModel.of(
          TEST_ACCOUNT_DTO_TWO,
          linkTo(methodOn(AccountController.class).getAccount(TEST_ACCOUNT_DTO_TWO.getId()))
              .withSelfRel(),
          linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS));

  public static final EntityModel<AccountDto> TEST_ACCOUNT_DTO_ENTITY_MODEL_THREE =
      EntityModel.of(
          TEST_ACCOUNT_DTO_ONE,
          linkTo(methodOn(AccountController.class).getAccount(TEST_ACCOUNT_DTO_THREE.getId()))
              .withSelfRel(),
          linkTo(methodOn(AccountController.class).getAllAccounts()).withRel(ALL_ACCOUNTS));

  /* Collection Model for Entity Models of Account DTOs */

  public static final CollectionModel<EntityModel<AccountDto>> TEST_ENTITY_MODEL_COLLECTION_MODEL =
      CollectionModel.of(
          List.of(
              TEST_ACCOUNT_DTO_ENTITY_MODEL_ONE,
              TEST_ACCOUNT_DTO_ENTITY_MODEL_TWO,
              TEST_ACCOUNT_DTO_ENTITY_MODEL_THREE),
          linkTo(methodOn(AccountController.class).getAllAccounts()).withSelfRel());

  /* Transaction DTOs */

  public static final Long TEST_TRANSACTION_ONE_ID = 1L;
  public static final Long TEST_TRANSACTION_TWO_ID = 2L;
  public static final Long TEST_TRANSACTION_THREE_ID = 3L;
  public static final Long TEST_TRANSACTION_FOUR_ID = 4L;
  public static final Long TEST_TRANSACTION_FIVE_ID = 5L;


  public static final TransactionDto TEST_TRANSACTION_DTO_ONE =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_ONE_ID)
                  .sourceAccountId(TEST_ACCOUNT_ONE_ID)
                  .targetAccountId(TEST_ACCOUNT_TWO_ID)
                  .amount(new BigDecimal("20.01"))
                  .currency(Currency.IDR)
                  .build();
  public static final TransactionDto TEST_TRANSACTION_DTO_TWO =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_TWO_ID)
                  .sourceAccountId(TEST_ACCOUNT_ONE_ID)
                  .targetAccountId(TEST_ACCOUNT_TWO_ID)
                  .amount(new BigDecimal("19000.80"))
                  .currency(Currency.IDR)
                  .build();

  public static final TransactionDto TEST_TRANSACTION_DTO_THREE =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_THREE_ID)
                  .sourceAccountId(TEST_ACCOUNT_ONE_ID)
                  .targetAccountId(TEST_ACCOUNT_THREE_ID)
                  .amount(BigDecimal.ZERO) // Invalid value, put here for testing purposes.
                  .currency(Currency.USD)
                  .build();
  public static final TransactionDto TEST_TRANSACTION_DTO_FOUR =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_FOUR_ID)
                  .sourceAccountId(TEST_ACCOUNT_THREE_ID)
                  .targetAccountId(TEST_ACCOUNT_TWO_ID)
                  .amount(new BigDecimal("0.19")) 
                  .currency(Currency.AFA) // Currency different from target account's on purpose, to test for a relevant
                                          // Exception being thrown.
                  .build();
  public static final TransactionDto TEST_TRANSACTION_DTO_FIVE =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_FIVE_ID)
                  .sourceAccountId(TEST_ACCOUNT_TWO_ID)
                  .targetAccountId(TEST_ACCOUNT_TWO_ID)  // From an account to itself, for testing purposes.
                  .amount(BigDecimal.TEN)
                  .currency(Currency.IDR)
                  .build();

  /* Transactions */

  public static final Transaction TEST_TRANSACTION_ONE =
          Transaction.builder()
                  .id(TEST_TRANSACTION_DTO_ONE.getId())
                  .sourceAccountId(TEST_TRANSACTION_DTO_ONE.getSourceAccountId())
                  .targetAccountId(TEST_TRANSACTION_DTO_ONE.getTargetAccountId())
                  .amount(TEST_TRANSACTION_DTO_ONE.getAmount())
                  .currency(TEST_TRANSACTION_DTO_ONE.getCurrency())
                  .submittedAt(new Date(946083999998L))
                  .build();
  
  public static final Transaction TEST_TRANSACTION_TWO =
          Transaction.builder()
                  .id(TEST_TRANSACTION_DTO_TWO.getId())
                  .sourceAccountId(TEST_TRANSACTION_DTO_TWO.getSourceAccountId())
                  .targetAccountId(TEST_TRANSACTION_DTO_TWO.getTargetAccountId())
                  .amount(TEST_TRANSACTION_DTO_TWO.getAmount())
                  .currency(TEST_TRANSACTION_DTO_TWO.getCurrency())
                  .submittedAt(new Date(946083999999L))
                  .build();
  public static final Transaction TEST_TRANSACTION_THREE =
          Transaction.builder()
                  .id(TEST_TRANSACTION_DTO_THREE.getId())
                  .sourceAccountId(TEST_TRANSACTION_DTO_THREE.getSourceAccountId())
                  .targetAccountId(TEST_TRANSACTION_DTO_THREE.getTargetAccountId())
                  .amount(TEST_TRANSACTION_DTO_THREE.getAmount())
                  .currency(TEST_TRANSACTION_DTO_THREE.getCurrency())
                  .submittedAt(new Date(946084000000L))
                  .build();
  public static final Transaction TEST_TRANSACTION_FOUR =
          Transaction.builder()
                  .id(TEST_TRANSACTION_DTO_FOUR.getId())
                  .sourceAccountId(TEST_TRANSACTION_DTO_FOUR.getSourceAccountId())
                  .targetAccountId(TEST_TRANSACTION_DTO_FOUR.getTargetAccountId())
                  .amount(TEST_TRANSACTION_DTO_FOUR.getAmount())
                  .currency(TEST_TRANSACTION_DTO_FOUR.getCurrency())
                  .submittedAt(new Date(946084000001L))
                  .build();

  /* Entity Models over Transaction DTOs */
  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE =
      EntityModel.of(
          TEST_TRANSACTION_DTO_ONE,
          linkTo(
                  methodOn(TransactionController.class)
                      .getTransaction(TEST_TRANSACTION_DTO_ONE.getId()))
              .withSelfRel(),
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_ONE.getSourceAccountId().toString(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_ONE.getTargetAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO =
      EntityModel.of(
          TEST_TRANSACTION_DTO_TWO,
          linkTo(
                  methodOn(TransactionController.class)
                      .getTransaction(TEST_TRANSACTION_DTO_TWO.getId()))
              .withSelfRel(),
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_TWO.getSourceAccountId().toString(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_TWO.getTargetAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE =
      EntityModel.of(
          TEST_TRANSACTION_DTO_THREE,
          linkTo(
                  methodOn(TransactionController.class)
                      .getTransaction(TEST_TRANSACTION_DTO_THREE.getId()))
              .withSelfRel(),
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_THREE.getSourceAccountId().toString(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_THREE.getTargetAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR =
      EntityModel.of(
          TEST_TRANSACTION_DTO_FOUR,
          linkTo(
                  methodOn(TransactionController.class)
                      .getTransaction(TEST_TRANSACTION_DTO_FOUR.getId()))
              .withSelfRel(),
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_FOUR.getSourceAccountId().toString(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_FOUR.getTargetAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  /* Collection Models over Entity Models of Transaction DTOs */

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_FULL =
          CollectionModel.of(
              List.of(
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(Collections.emptyMap()))
                  .withSelfRel());

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE =
          CollectionModel.of(
              List.of(
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(
                              Map.of(SOURCE_ACCOUNT_ID, TEST_ACCOUNT_DTO_ONE.getId().toString())))
                  .withSelfRel(),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(Collections.emptyMap()))
                  .withRel(ALL_TRANSACTIONS_BETWEEN));

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_TO_ACCOUNT_TWO =
          CollectionModel.of(
              List.of(
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(
                              Map.of(TARGET_ACCOUNT_ID, TEST_ACCOUNT_DTO_TWO.getId().toString())))
                  .withSelfRel(),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(Collections.emptyMap()))
                  .withRel(ALL_TRANSACTIONS_BETWEEN));

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE_TO_ACCOUNT_TWO =
          CollectionModel.of(
              List.of(TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(
                              Map.of(
                                  SOURCE_ACCOUNT_ID,
                                  TEST_ACCOUNT_DTO_ONE.getId().toString(),
                                  TARGET_ACCOUNT_ID,
                                  TEST_ACCOUNT_DTO_TWO.getId().toString())))
                  .withSelfRel(),
              linkTo(
                      methodOn(TransactionController.class)
                          .getAllTransactions(Collections.emptyMap()))
                  .withRel(ALL_TRANSACTIONS_BETWEEN));

  /* Exchange rate Map for mocked CurrencyLedger calls */

  public static final Map<CurrencyPair, BigDecimal> TEST_EXCHANGE_RATES =
      Map.of(
          new CurrencyPair(Currency.GBP, Currency.IDR), BigDecimal.TEN,
          new CurrencyPair(Currency.GBP, Currency.USD), BigDecimal.ONE,
          new CurrencyPair(Currency.USD, Currency.IDR), new BigDecimal("5.65"),
          new CurrencyPair(Currency.USD, Currency.USD), BigDecimal.ONE,
          new CurrencyPair(Currency.GBP, Currency.GBP), BigDecimal.ONE,
          new CurrencyPair(Currency.IDR, Currency.IDR), BigDecimal.ONE);
}
