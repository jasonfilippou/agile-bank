package com.agilebank.model.currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class CurrencyLedger {
  private final Random random = new Random(47); // Keep the "randomness" consistent across runs of the app
  private static Map<CurrencyPair, BigDecimal> currencyExchangeRates;

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
            currencyOne == currencyTwo ? BigDecimal.ONE : BigDecimal.valueOf(Double.parseDouble(String.format("%.2f", 100 * random.nextDouble()))));
      }
    }
    return currencyExchangeRates;
  }

  @AllArgsConstructor
  @EqualsAndHashCode
  @Getter
  public static class CurrencyPair {
    private Currency currencyOne;
    private Currency currencyTwo;
    
    @Override
    public String toString(){
      return "<" + currencyOne + ", " + currencyTwo + ">"; 
    }
  }
}