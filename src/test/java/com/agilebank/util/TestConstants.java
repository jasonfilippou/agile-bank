package com.agilebank.util;

import static com.agilebank.controller.TransactionController.SOURCE_ACCOUNT_ID;
import static com.agilebank.controller.TransactionController.TARGET_ACCOUNT_ID;
import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.agilebank.controller.AccountController;
import com.agilebank.controller.TransactionController;
import com.agilebank.model.account.AccountDao;
import com.agilebank.model.account.AccountDto;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.transaction.TransactionDao;
import com.agilebank.model.transaction.TransactionDto;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public class TestConstants {

  /* Account IDs */
  public static final String ACCOUNT_ONE_ID = "acc1";
  public static final String ACCOUNT_TWO_ID = "acc2";
  public static final String ACCOUNT_THREE_ID = "acc3";

  /* Account DTOs */

  public static final AccountDto TEST_ACCOUNT_DTO_ONE =
      new AccountDto(ACCOUNT_ONE_ID, new BigDecimal("120.25"), Currency.GBP);

  public static final AccountDto TEST_ACCOUNT_DTO_TWO =
      new AccountDto(ACCOUNT_TWO_ID, new BigDecimal("801.01"), Currency.IDR);

  public static final AccountDto TEST_ACCOUNT_DTO_THREE =
      new AccountDto(ACCOUNT_THREE_ID, new BigDecimal("-51.00"), Currency.USD); // Invalid value put here for testing.
  
  /* Account DAOs */

  public static final AccountDao TEST_ACCOUNT_DAO_ONE =
      new AccountDao(
          TEST_ACCOUNT_DTO_ONE.getId(),
          TEST_ACCOUNT_DTO_ONE.getBalance(),
          TEST_ACCOUNT_DTO_ONE.getCurrency(),
          new Date(946083999988L));

  public static final AccountDao TEST_ACCOUNT_DAO_TWO =
      new AccountDao(
          TEST_ACCOUNT_DTO_TWO.getId(),
          TEST_ACCOUNT_DTO_TWO.getBalance(),
          TEST_ACCOUNT_DTO_TWO.getCurrency(),
          new Date(946083999989L));

  public static final AccountDao TEST_ACCOUNT_DAO_THREE =
      new AccountDao(
          TEST_ACCOUNT_DTO_THREE.getId(),
          TEST_ACCOUNT_DTO_THREE.getBalance(),
          TEST_ACCOUNT_DTO_THREE.getCurrency(),
          new Date(946083999990L));

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

  public static final TransactionDto TEST_TRANSACTION_DTO_ONE =
      new TransactionDto(ACCOUNT_ONE_ID, ACCOUNT_TWO_ID, new BigDecimal("20.01"), Currency.IDR);

  public static final TransactionDto TEST_TRANSACTION_DTO_TWO =
      new TransactionDto(ACCOUNT_ONE_ID, ACCOUNT_TWO_ID, new BigDecimal("19000.80"), Currency.IDR);

  public static final TransactionDto TEST_TRANSACTION_DTO_THREE =
      new TransactionDto(ACCOUNT_ONE_ID, ACCOUNT_THREE_ID, BigDecimal.ZERO, Currency.USD);

  public static final TransactionDto TEST_TRANSACTION_DTO_FOUR =
      new TransactionDto(
          ACCOUNT_THREE_ID,
          ACCOUNT_TWO_ID,
          new BigDecimal("0.19"),
          Currency
              .AFA); // Currency different from target account's on purpose, to test for a relevant
                     // Exception being thrown.

  public static final TransactionDto TEST_TRANSACTION_FROM_ACCOUNT_TO_ITSELF =
          new TransactionDto(
                  ACCOUNT_TWO_ID, ACCOUNT_TWO_ID, BigDecimal.TEN, Currency.IDR); // Again, an instance only for testing.
  
  /* Transaction DAOs */

  public static final TransactionDao TEST_TRANSACTION_DAO_ONE =
      new TransactionDao(
          TEST_TRANSACTION_DTO_ONE.getSourceAccountId(),
          TEST_TRANSACTION_DTO_ONE.getTargetAccountId(),
          TEST_TRANSACTION_DTO_ONE.getAmount(),
          TEST_TRANSACTION_DTO_ONE.getCurrency(),
          new Date(946083999998L));
  public static final TransactionDao TEST_TRANSACTION_DAO_TWO =
      new TransactionDao(
          TEST_TRANSACTION_DTO_TWO.getSourceAccountId(),
          TEST_TRANSACTION_DTO_TWO.getTargetAccountId(),
          TEST_TRANSACTION_DTO_TWO.getAmount(),
          TEST_TRANSACTION_DTO_TWO.getCurrency(),
          new Date(946083999999L));

  public static final TransactionDao TEST_TRANSACTION_DAO_THREE =
      new TransactionDao(
          TEST_TRANSACTION_DTO_THREE.getSourceAccountId(),
          TEST_TRANSACTION_DTO_THREE.getTargetAccountId(),
          TEST_TRANSACTION_DTO_THREE.getAmount(),
          TEST_TRANSACTION_DTO_THREE.getCurrency(),
          new Date(946084000000L));

  public static final TransactionDao TEST_TRANSACTION_DAO_FOUR =
      new TransactionDao(
          TEST_TRANSACTION_DTO_FOUR.getSourceAccountId(),
          TEST_TRANSACTION_DTO_FOUR.getTargetAccountId(),
          TEST_TRANSACTION_DTO_FOUR.getAmount(),
          TEST_TRANSACTION_DTO_FOUR.getCurrency(),
          new Date(946084000001L));

  /* Constants useful for the Entity and Collection Models of Transaction DTOs */

  public static final String ALL_TRANSACTIONS_BETWEEN = "all_transactions_between";
  public static final String ALL_TRANSACTIONS = "all_transactions";

  /* Entity Models over Transaction DTOs */
  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE =
      EntityModel.of(
          TEST_TRANSACTION_DTO_ONE,
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_ONE.getSourceAccountId(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_ONE.getTargetAccountId())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO =
      EntityModel.of(
          TEST_TRANSACTION_DTO_TWO,
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_TWO.getSourceAccountId(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_TWO.getTargetAccountId())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE =
      EntityModel.of(
          TEST_TRANSACTION_DTO_THREE,
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_THREE.getSourceAccountId(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_THREE.getTargetAccountId())))
              .withRel(ALL_TRANSACTIONS_BETWEEN),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));

  public static final EntityModel<TransactionDto> TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR =
      EntityModel.of(
          TEST_TRANSACTION_DTO_FOUR,
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_FOUR.getSourceAccountId(),
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_FOUR.getTargetAccountId())))
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
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR));

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE =
          CollectionModel.of(
              List.of(
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_THREE));

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_TO_ACCOUNT_TWO =
          CollectionModel.of(
              List.of(
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO,
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_FOUR));

  public static final CollectionModel<EntityModel<TransactionDto>>
      TEST_ENTITY_MODEL_COLLECTION_MODEL_FROM_ACCOUNT_ONE_TO_ACCOUNT_TWO =
          CollectionModel.of(
              List.of(
                  TEST_TRANSACTION_DTO_ENTITY_MODEL_ONE, TEST_TRANSACTION_DTO_ENTITY_MODEL_TWO));

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
