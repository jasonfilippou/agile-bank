package com.agilebank.model.currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * A utility class that generates randomly defined exchange rates between {@link Currency} instances.
 * 
 * @author jason 
 * 
 * @see Currency
 */
@Component
public class CurrencyLedger {
  private static Map<CurrencyPair, BigDecimal> currencyExchangeRates;
  private final Random random =
      new Random(47); // Keep the "randomness" consistent across runs of the app

  /**
   * Constructs the full list of exchange rates between all pairs of {@link Currency} instances. The exchange rates are 
   * pseudo-random {@link BigDecimal} instances in the interval (0, 100], except for pairs of a {@link Currency} with itself,
   * where we force the value of {@link BigDecimal#ONE}.
   * @return A {@link Map} with {@link CurrencyPair}s as keys and pseudo-random {@link BigDecimal}s as values.
   * @see CurrencyPair
   */
  public Map<CurrencyPair, BigDecimal> getCurrencyExchangeRates() {
    if (currencyExchangeRates == null) {
      currencyExchangeRates = createCurrencyExchangeRates();
    }
    return currencyExchangeRates;
  }

  private Map<CurrencyPair, BigDecimal> createCurrencyExchangeRates() {
    Map<CurrencyPair, BigDecimal> currencyExchangeRates = new HashMap<>();
    for (Currency currencyOne : Currency.values()) {
      for (Currency currencyTwo : Currency.values()) {
        currencyExchangeRates.put(
            new CurrencyPair(currencyOne, currencyTwo),
            currencyOne == currencyTwo
                ? BigDecimal.ONE
                : BigDecimal.valueOf(
                    Double.parseDouble(String.format("%.2f", random.nextDouble(0.01, 100)))));
      }
    }
    return currencyExchangeRates;
  }

  /**
   * A utility that converts a given amount from a given source {@link Currency} to a target {@link Currency}
   * @param sourceCurrency The source {@link Currency}.
   * @param targetCurrency The target {@link Currency}
   * @param amount The amount to convert.
   * @return The amount in the new {@link Currency}, based on what {@link  CurrencyLedger} has stored.
   */
  public BigDecimal convertAmountToTargetCurrency(Currency sourceCurrency, Currency targetCurrency, BigDecimal amount){
    if (currencyExchangeRates == null) {
      currencyExchangeRates = createCurrencyExchangeRates();
    }
    return currencyExchangeRates.get(CurrencyPair.of(sourceCurrency, targetCurrency)).multiply(amount);
  }

  /**
   * Simple POJO for defining ordered pairs of {@link Currency} instances.
   * @see java.util.Currency
   */
  @AllArgsConstructor
  @EqualsAndHashCode
  @Getter
  public static class CurrencyPair {
    private Currency currencyOne;
    private Currency currencyTwo;

    @Override
    public String toString() {
      return "<" + currencyOne + ", " + currencyTwo + ">";
    }

    public static CurrencyPair of(Currency first, Currency second){
      return new CurrencyPair(first, second);
    }
  }
}
