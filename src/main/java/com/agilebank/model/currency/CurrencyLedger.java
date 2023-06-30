package com.agilebank.model.currency;

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
  private static Map<CurrencyPair, Double> currencyExchangeRates;

  public Map<CurrencyPair, Double> getCurrencyExchangeRates() {
    if (currencyExchangeRates == null) {
      currencyExchangeRates = createCurrencyExchangeRates();
    }
    return currencyExchangeRates;
  }

  private Map<CurrencyPair, Double> createCurrencyExchangeRates() {
    Map<CurrencyPair, Double> currencyExchangeRates = new HashMap<>();
    for (Currency currencyOne : Currency.values()) {
      for (Currency currencyTwo : Currency.values()) {
        currencyExchangeRates.put(
                new CurrencyPair(currencyOne, currencyTwo),
            currencyOne == currencyTwo ? 1.0 :  Double.parseDouble(String.format("%.2f", 100 * random.nextDouble())));
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
