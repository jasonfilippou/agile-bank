package com.agilebank.util;

/**
 * Various constants used by the code.
 * @author jason
 */
public final class Constants {
  public static final String SOURCE_ACCOUNT_ID = "sourceAccountId";
  public static final String TARGET_ACCOUNT_ID = "targetAccountId";
  public static final String AUTH_HEADER_BEARER_PREFIX = "Bearer" + " ";
  public static final String ALL_ACCOUNTS = "all_accounts";
  public static final String ALL_TRANSACTIONS = "all_transactions";

  public static final String ALL_TRANSACTIONS_FROM = "all_transactions_from";
  public static final String ALL_TRANSACTIONS_TO = "all_transactions_to";
  public static final String ALL_TRANSACTIONS_BETWEEN = "all_transactions_between";

  public static final String DEFAULT_PAGE_IDX = "0";
  public static final String DEFAULT_PAGE_SIZE = "5";
  public static final String DEFAULT_SORT_BY_FIELD = "id";
  public static final String DEFAULT_SORT_ORDER = "ASC";

  /**
   * Tune this to affect how long the JWT token lasts. Default is 5 * 60 * 60, for 5 hours.
   */
  public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 hours
  private Constants() {}
}
