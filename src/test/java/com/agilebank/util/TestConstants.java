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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public final class TestConstants {

  /* A Random instance used in constants */

  public static final Random RANDOM = new Random(47);

  /* A string that is used a lot in ModelAssembler calls */

  public static final String ALL_ACCOUNTS = "all_accounts";

  /* Utilities */

  private static BigDecimal randomPositiveAmount(){
    return BigDecimal.valueOf(Double.parseDouble(String.format("%.2f", RANDOM.nextDouble() + 0.01))); // Minimum of a cent of whatever currency we are in.
  }

  private static final Long INTERVAL_LENGTH = 10L;

  private static final Long LOW_INITIALLY = 1L;

  private static final Long HIGH_INITIALLY = LOW_INITIALLY + (INTERVAL_LENGTH - 1);
  private static Long low = LOW_INITIALLY;
  private static Long high = HIGH_INITIALLY;

  private static List<Long> generateIntervalAndAdvanceEndpoints(){
    List<Long> retVal = LongStream.rangeClosed(low, high).boxed().toList();
    low = high + 1;
    high += INTERVAL_LENGTH;
    return retVal;
  }

  private static List<Long> generateIntervalAndResetEndpoints(){
    List<Long> retVal = LongStream.rangeClosed(low, high).boxed().toList();
    resetEndpoints();
    return retVal;
  }

  private static void resetEndpoints(){
    low = LOW_INITIALLY;
    high = HIGH_INITIALLY;
  }

  /* Account IDs */

  public static final Map<Currency, List<Long>> TEST_GIVEN_CURRENCY_ACCOUNT_IDS = Map.of(
          Currency.USD, generateIntervalAndAdvanceEndpoints(),
          Currency.GBP, generateIntervalAndAdvanceEndpoints(),
          Currency.EUR, generateIntervalAndAdvanceEndpoints(),
          Currency.INR, generateIntervalAndResetEndpoints()
  );


  /* Account DTOs */
  public static final Map<Currency, List<AccountDto>> ACCOUNT_DTOS_OF_GIVEN_CURRENCIES =
          TEST_GIVEN_CURRENCY_ACCOUNT_IDS
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                            .map(id -> AccountDto.builder()
                                    .id(id)
                                    .balance(randomPositiveAmount())
                                    .currency(entry.getKey())
                                    .build())
                            .toList()));
  
  /* Accounts */
  public static final Map<Currency, List<Account>> ACCOUNT_ENTITIES_OF_GIVEN_CURRENCIES =
          ACCOUNT_DTOS_OF_GIVEN_CURRENCIES
              .entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey,
                      entry -> entry.getValue().stream()
                              .map(accountDto ->
                                      Account.builder()
                                              .id(accountDto.getId())
                                              .balance(accountDto.getBalance())
                                              .currency(accountDto.getCurrency())
                                              .createdAt(new Date())
                                              .build())
                              .toList()));

  /* Entity Models for Account DTOs */

  public static final Map<Currency, List<EntityModel<AccountDto>>> TEST_ACCOUNT_DTO_ENTITY_MODELS =
          ACCOUNT_DTOS_OF_GIVEN_CURRENCIES
                  .entrySet().stream()
                  .collect(Collectors.toMap(Map.Entry::getKey,
                          entry -> entry.getValue().stream()
                                  .map(accountDto ->
                                          EntityModel.of(accountDto,
                                            linkTo(methodOn(AccountController.class).getAccount(accountDto.getId()))
                                                    .withSelfRel(),
                                            linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX),
                                                    Integer.parseInt(DEFAULT_PAGE_SIZE), DEFAULT_SORT_BY_FIELD, SortOrder.ASC)).withRel(ALL_ACCOUNTS)))
                                  .toList()));

  /* Collection Model for Entity Models of Account DTOs */

  public static final Map<Currency, CollectionModel<EntityModel<AccountDto>>> TEST_ACCOUNT_DTO_ENTITY_MODEL_COLLECTION_MODELS =
          TEST_ACCOUNT_DTO_ENTITY_MODELS
                  .entrySet().stream()
                  .collect(Collectors.toMap(Map.Entry::getKey,
                          entry->CollectionModel.of(
                                  entry.getValue(),
                                  linkTo(methodOn(AccountController.class).getAllAccounts(Integer.parseInt(DEFAULT_PAGE_IDX),
                                          Integer.parseInt(DEFAULT_PAGE_SIZE), DEFAULT_SORT_BY_FIELD, SortOrder.ASC)).withSelfRel())));

  /* Transaction IDs */

  public static final Map<CurrencyPair, List<Long>> TEST_GIVEN_CURRENCY_TRANSACTION_IDS =
          TEST_GIVEN_CURRENCY_ACCOUNT_IDS.keySet().stream()
          .flatMap(currencyOne -> TEST_GIVEN_CURRENCY_ACCOUNT_IDS.keySet().stream()
                  .map(currencyTwo -> CurrencyPair.of(currencyOne, currencyTwo)))
          .map(currencyPair -> Map.entry(currencyPair, generateIntervalAndAdvanceEndpoints()))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

  /* Transaction DTOs */

  public static final List<TransactionDto> TEST_TRANSACTION_DTOS =
          TEST_GIVEN_CURRENCY_TRANSACTION_IDS.entrySet().stream()
                  .collect(Collectors.toMap(Map.Entry::getKey,
                                  entry -> entry.getValue().stream()
                                          .map(id -> TransactionDto.builder()))

                  /*  public static final Map<Currency, List<AccountDto>> ACCOUNT_DTOS_OF_GIVEN_CURRENCIES =
          TEST_GIVEN_CURRENCY_ACCOUNT_IDS
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                            .map(id -> AccountDto.builder()
                                    .id(id)
                                    .balance(randomPositiveAmount())
                                    .currency(entry.getKey())
                                    .build())
                            .toList())); */
  public static final TransactionDto TEST_TRANSACTION_DTO_FOUR =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_FOUR_ID)
                  .sourceAccountId(TEST_ACCOUNT_THREE_ID)
                  .targetAccountId(TEST_ACCOUNT_TWO_ID)
                  .amount(new BigDecimal("0.19"))
                  .currency(
                          Currency.AFA) // Currency different from target account's on purpose, to test for a
                  // relevant
                  // Exception being thrown.
                  .build();

  public static final TransactionDto TEST_TRANSACTION_DTO_FIVE =
          TransactionDto.builder()
                  .id(TEST_TRANSACTION_FIVE_ID)
                  .sourceAccountId(TEST_ACCOUNT_TWO_ID)
                  .targetAccountId(TEST_ACCOUNT_TWO_ID) // From an account to itself, for testing purposes.
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
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_ONE.getSourceAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_FROM),
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_ONE.getTargetAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_TO),
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
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              SOURCE_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_TWO.getSourceAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_FROM),
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Map.of(
                              TARGET_ACCOUNT_ID,
                              TEST_TRANSACTION_DTO_TWO.getTargetAccountId().toString())))
              .withRel(ALL_TRANSACTIONS_TO),
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
                  linkTo(
                          methodOn(TransactionController.class)
                                  .getAllTransactions(
                                          Map.of(
                                                  SOURCE_ACCOUNT_ID,
                                                  TEST_TRANSACTION_DTO_THREE.getSourceAccountId().toString())))
                          .withRel(ALL_TRANSACTIONS_FROM),
                  linkTo(
                          methodOn(TransactionController.class)
                                  .getAllTransactions(
                                          Map.of(
                                                  TARGET_ACCOUNT_ID,
                                                  TEST_TRANSACTION_DTO_THREE.getTargetAccountId().toString())))
                          .withRel(ALL_TRANSACTIONS_TO),
                  linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
                          .withRel(ALL_TRANSACTIONS));
  
  /* Collection Models over Entity Models of Transaction DTOs */
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
              linkTo(methodOn(TransactionController.class)
                      .getAllTransactions(Map.of(SOURCE_ACCOUNT_ID, TEST_TRANSACTION_DTO_FOUR.getSourceAccountId().toString()))).withRel(ALL_TRANSACTIONS_FROM),
              linkTo(methodOn(TransactionController.class)
                      .getAllTransactions(Map.of(TARGET_ACCOUNT_ID, TEST_TRANSACTION_DTO_FOUR.getTargetAccountId().toString()))).withRel(ALL_TRANSACTIONS_TO),
          linkTo(methodOn(TransactionController.class).getAllTransactions(Collections.emptyMap()))
              .withRel(ALL_TRANSACTIONS));
  
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

  /* Exchange rate Map for mocked CurrencyLedger calls */

  public static final Map<CurrencyPair, BigDecimal> TEST_EXCHANGE_RATES =
      Map.of(
          new CurrencyPair(Currency.GBP, Currency.IDR), BigDecimal.TEN,
          new CurrencyPair(Currency.GBP, Currency.USD), BigDecimal.ONE,
          new CurrencyPair(Currency.USD, Currency.IDR), new BigDecimal("5.65"),
          new CurrencyPair(Currency.USD, Currency.USD), BigDecimal.ONE,
          new CurrencyPair(Currency.GBP, Currency.GBP), BigDecimal.ONE,
          new CurrencyPair(Currency.IDR, Currency.IDR), BigDecimal.ONE);

  private TestConstants() {}
}
