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
import com.google.common.collect.Comparators;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public final class TestUtils {

  /* A Random instance used in constants */

  public static final Random RANDOM = new Random(47);

  /* A string that is used a lot in ModelAssembler calls */

  public static final String ALL_ACCOUNTS = "all_accounts";

  /* A balance to instantiate accounts with */

  private static final BigDecimal STANDARD_ACCOUNT_BALANCE = new BigDecimal("1000.00");

  /* Account DTOs */

  // We will give an ID to the AccountDto instances we create because it will make our tests
  // for sorting by ID cleaner to write.
  private static Long currentAccountId = 1L;
  
  private static AccountDto accountDtoOfGivenParams(Currency currency, BigDecimal balance) {
    return AccountDto.builder().id(currentAccountId++).balance(balance).currency(currency).build();
  }

  public static final List<AccountDto> TEST_ACCOUNT_DTOS =
      List.of(

          // 5 USD accounts
          accountDtoOfGivenParams(Currency.USD, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.USD, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.USD, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.USD, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.USD, STANDARD_ACCOUNT_BALANCE),

          // 5 GBP accounts
          accountDtoOfGivenParams(Currency.GBP, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.GBP, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.GBP, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.GBP, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.GBP, STANDARD_ACCOUNT_BALANCE),

          // 5 EUR accounts
          accountDtoOfGivenParams(Currency.EUR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.EUR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.EUR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.EUR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.EUR, STANDARD_ACCOUNT_BALANCE),

          // 5 INR accounts
          accountDtoOfGivenParams(Currency.INR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.INR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.INR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.INR, STANDARD_ACCOUNT_BALANCE),
          accountDtoOfGivenParams(Currency.INR, STANDARD_ACCOUNT_BALANCE));

  /* Accounts */
  
  private static Account accountFromAccountDto(AccountDto accountDto) {
    return Account.builder()
        .id(accountDto.getId())
        .currency(accountDto.getCurrency())
        .balance(accountDto.getBalance())
        .build();
  }

  public static final List<Account> TEST_ACCOUNT_ENTITIES =
      TEST_ACCOUNT_DTOS.stream().map(TestUtils::accountFromAccountDto).collect(Collectors.toList());

  /* Entity Models for Account DTOs */

  private static EntityModel<AccountDto> accountDtoToEntityModel(AccountDto accountDto) {
    return EntityModel.of(
        accountDto,
        linkTo(methodOn(AccountController.class).getAccount(accountDto.getId())).withSelfRel(),
        linkTo(
                methodOn(AccountController.class)
                    .getAllAccounts(
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_ACCOUNTS));
  }

  public static final List<EntityModel<AccountDto>> TEST_ACCOUNT_DTO_ENTITY_MODELS =
      TEST_ACCOUNT_DTOS.stream()
          .map(TestUtils::accountDtoToEntityModel)
          .collect(Collectors.toList());

  /* Generate a Collection Model for Entity Models of Account DTOs on demand, depending on how many
   * entities your pagination returned. */

  public static CollectionModel<EntityModel<AccountDto>> accountDtosToCollectionModel(
      List<AccountDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream()
            .map(TestUtils::accountDtoToEntityModel)
            .collect(Collectors.toList()),
        linkTo(
                methodOn(AccountController.class)
                    .getAllAccounts(
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withSelfRel());
  }

  /* Valid Transaction DTOs */

  private static final Long currentTransactionId = 1L;
  private static TransactionDto transactionDtoOfGivenParams(
      Long sourceAccountId, Long targetAccountId, Currency currency, BigDecimal amount) {
    return TransactionDto.builder()
        .id(currentAccountId++)
        .sourceAccountId(sourceAccountId)
        .targetAccountId(targetAccountId)
        .amount(amount)
        .currency(currency)
        .build();
  }

  private static final BigDecimal STANDARD_TRANSACTION_AMOUNT =
      STANDARD_ACCOUNT_BALANCE.divide(new BigDecimal("10"), RoundingMode.HALF_EVEN);

  // We will make the following transactions valid by mocking the currency exchange between the
  // affected currencies to be 1. They also satisfy the other constraints of transactions by construction.
  // We will also give an ID to the AccountDto instances we create because it will make our tests
  // for sorting by ID cleaner to write.
  
  public static final List<TransactionDto> TEST_VALID_TRANSACTION_DTOS =
      List.of(

          /* 16 from the USD account set to all sets */

          // 4 from account 1
          transactionDtoOfGivenParams(1L, 2L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(1L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(1L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(1L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 2
          transactionDtoOfGivenParams(2L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(2L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(2L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(2L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 3
          transactionDtoOfGivenParams(3L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(3L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(3L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(3L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 4
          transactionDtoOfGivenParams(4L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(4L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(4L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(4L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          /* 16 from the GBP account set to all sets */

          // 4 from account 6
          transactionDtoOfGivenParams(6L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(6L, 7L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(6L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(6L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 7
          transactionDtoOfGivenParams(7L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(7L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(7L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(7L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 8
          transactionDtoOfGivenParams(8L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(8L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(8L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(8L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 9
          transactionDtoOfGivenParams(9L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(9L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(9L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(9L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          /* 16 from the EUR account set to all sets */

          // 4 from account 11
          transactionDtoOfGivenParams(11L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(11L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(11L, 12L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(11L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 12
          transactionDtoOfGivenParams(12L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(12L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(12L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(12L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 13
          transactionDtoOfGivenParams(13L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(13L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(13L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(13L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 14
          transactionDtoOfGivenParams(14L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(14L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(14L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(14L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          /* 16 from the INR account set to all sets */

          // 4 from account 16
          transactionDtoOfGivenParams(16L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(16L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(16L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(16L, 17L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 17
          transactionDtoOfGivenParams(17L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(17L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(17L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(17L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 18
          transactionDtoOfGivenParams(18L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(18L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(18L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(18L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT),

          // 4 from account 19
          transactionDtoOfGivenParams(19L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(19L, 6L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(19L, 11L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(19L, 16L, Currency.INR, STANDARD_TRANSACTION_AMOUNT));

  /* Invalid Transaction DTOs */

  public static final List<TransactionDto> TEST_TRANSACTIONS_FROM_ACCOUNT_TO_ITSELF =
      List.of(
          transactionDtoOfGivenParams(5L, 5L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(10L, 10L, Currency.GBP, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(15L, 15L, Currency.EUR, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(20L, 20L, Currency.INR, STANDARD_TRANSACTION_AMOUNT));

  public static final List<TransactionDto> TEST_TRANSACTIONS_WITH_INVALID_CURRENCIES =
      List.of(
          transactionDtoOfGivenParams(1L, 6L, Currency.AFA, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(6L, 1L, Currency.AFA, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(11L, 16L, Currency.AFA, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(16L, 11L, Currency.AFA, STANDARD_TRANSACTION_AMOUNT));
  public static final List<TransactionDto> TEST_TRANSACTIONS_INVOLVING_NON_EXISTENT_ACCOUNTS =
      List.of(
          transactionDtoOfGivenParams(0L, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(1L, 0L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(
              currentAccountId, 1L, Currency.USD, STANDARD_TRANSACTION_AMOUNT),
          transactionDtoOfGivenParams(
              1L, currentAccountId, Currency.USD, STANDARD_TRANSACTION_AMOUNT));

  public static final List<TransactionDto>
      TEST_TRANSACTIONS_WITH_INSUFFICIENT_BALANCE_IN_SOURCE_ACCOUNT =
          List.of(
              transactionDtoOfGivenParams(
                  1L, 2L, Currency.USD, STANDARD_ACCOUNT_BALANCE.add(BigDecimal.ONE)),
              transactionDtoOfGivenParams(
                  1L, 6L, Currency.GBP, STANDARD_ACCOUNT_BALANCE.add(BigDecimal.ONE)),
              transactionDtoOfGivenParams(
                  1L, 11L, Currency.EUR, STANDARD_ACCOUNT_BALANCE.add(BigDecimal.ONE)),
              transactionDtoOfGivenParams(
                  1L, 17L, Currency.INR, STANDARD_ACCOUNT_BALANCE.add(BigDecimal.ONE)));

  /* Valid Transaction Entities */

  private static Transaction transactionDtoToEntity(TransactionDto transactionDto) {
    return Transaction.builder()
        .id(transactionDto.getId())
        .sourceAccountId(transactionDto.getSourceAccountId())
        .targetAccountId(transactionDto.getTargetAccountId())
        .amount(transactionDto.getAmount())
        .currency(transactionDto.getCurrency())
        .build();
  }

  public static final List<Transaction> TEST_VALID_TRANSACTION_ENTITIES =
      TEST_VALID_TRANSACTION_DTOS.stream()
          .map(TestUtils::transactionDtoToEntity)
          .collect(Collectors.toList());

  /* Entity Models over Valid Transaction DTOs */

  private static EntityModel<TransactionDto> transactionDtoToEntityModel(
      TransactionDto transactionDto) {
    return EntityModel.of(
        transactionDto,
        linkTo(methodOn(TransactionController.class).getTransaction(transactionDto.getId()))
            .withSelfRel(),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(SOURCE_ACCOUNT_ID, transactionDto.getSourceAccountId().toString()),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS_FROM),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(TARGET_ACCOUNT_ID, transactionDto.getTargetAccountId().toString()),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS_TO),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Map.of(
                            SOURCE_ACCOUNT_ID,
                            Long.toString(transactionDto.getSourceAccountId()),
                            TARGET_ACCOUNT_ID,
                            Long.toString(transactionDto.getTargetAccountId())),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS_BETWEEN),
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        Collections.emptyMap(),
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withRel(ALL_TRANSACTIONS));
  }

  public static final List<EntityModel<TransactionDto>> TEST_VALID_TRANSACTION_DTO_ENTITY_MODELS =
      TEST_VALID_TRANSACTION_DTOS.stream()
          .map(TestUtils::transactionDtoToEntityModel)
          .collect(Collectors.toList());

  /* Generate a Collection Model for Entity Models of Account DTOs on demand, depending on how many
   * entities your pagination returned. */

  public static CollectionModel<EntityModel<TransactionDto>> transactionDtosToCollectionModel(
      List<TransactionDto> entities) {
    return CollectionModel.of(
        IterableUtils.toList(entities).stream()
            .map(TestUtils::transactionDtoToEntityModel)
            .collect(Collectors.toList()));
  }

  public static CollectionModel<EntityModel<TransactionDto>> transactionDtosToCollectionModel(
      List<TransactionDto> entities, Map<String, String> params) {
    CollectionModel<EntityModel<TransactionDto>> collectionModel = transactionDtosToCollectionModel(entities);
    collectionModel.add(
        linkTo(
                methodOn(TransactionController.class)
                    .getAllTransactions(
                        params,
                        Integer.parseInt(DEFAULT_PAGE_IDX),
                        Integer.parseInt(DEFAULT_PAGE_SIZE),
                        DEFAULT_SORT_BY_FIELD,
                        SortOrder.ASC))
            .withSelfRel());
    if (params.size() > 0) {
      collectionModel.add(
          linkTo(
                  methodOn(TransactionController.class)
                      .getAllTransactions(
                          Collections.emptyMap(),
                          Integer.parseInt(DEFAULT_PAGE_IDX),
                          Integer.parseInt(DEFAULT_PAGE_SIZE),
                          DEFAULT_SORT_BY_FIELD,
                          SortOrder.ASC))
              .withRel(ALL_TRANSACTIONS));
    }
    return collectionModel;
  }

  /* Exchange rate Map for mocked CurrencyLedger calls */

  public static final Map<CurrencyPair, BigDecimal> TEST_EXCHANGE_RATES =
      Map.ofEntries(
          Map.entry(CurrencyPair.of(Currency.USD, Currency.USD), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.USD, Currency.GBP), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.USD, Currency.EUR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.USD, Currency.INR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.GBP, Currency.USD), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.GBP, Currency.GBP), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.GBP, Currency.EUR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.GBP, Currency.INR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.EUR, Currency.USD), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.EUR, Currency.GBP), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.EUR, Currency.EUR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.EUR, Currency.INR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.INR, Currency.USD), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.INR, Currency.GBP), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.INR, Currency.EUR), BigDecimal.ONE),
          Map.entry(CurrencyPair.of(Currency.INR, Currency.INR), BigDecimal.ONE));  
  
  
  /* Some utilities for comparing fields of arbitrary POJOs */

  public static boolean collectionIsSortedByFieldInGivenDirection(Collection<?> pojos,
                                                            String sortByField, SortOrder sortOrder){
    // Using a Guava dependency here
    return Comparators.isInOrder(pojos, (p1, p2) -> compareFieldsInGivenOrder(p1.getClass(), p2.getClass(), 
            sortByField, sortOrder));
  }

  public static <T extends Comparable<T>> int compareFieldsInGivenOrder(Class<?> pojoOne, Class<?> pojoTwo,
                                                                  String sortByField, SortOrder sortOrder){
    try {
      assert pojoOne.equals(pojoTwo);
      PropertyDescriptor propertyDescriptor = new PropertyDescriptor(sortByField, pojoOne);
      Method appropriateGetter = propertyDescriptor.getReadMethod();
      @SuppressWarnings("unchecked")
      T pojoOneFieldValue = (T) appropriateGetter.invoke(pojoOne);
      @SuppressWarnings("unchecked")
      T pojoTwoFieldValue = (T) appropriateGetter.invoke(pojoTwo);
      return sortOrder == SortOrder.ASC ? pojoOneFieldValue.compareTo(pojoTwoFieldValue) :
              pojoTwoFieldValue.compareTo(pojoOneFieldValue);
    } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e.getMessage());
    } catch(ClassCastException exc){
      throw new RuntimeException("Field " + sortByField + " of " + pojoOne.getSimpleName()
              + " is not Comparable.");
    }
  }
  private TestUtils() {}
}
