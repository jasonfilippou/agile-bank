package com.agilebank.model.currency;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class CurrencyLedger {
  private static final Random random = new Random();
  private static Map<CurrencyPair, Double> currencyLedger;

  public static Map<CurrencyPair, Double> getCurrencyLedger() {
    if (currencyLedger == null) {
      currencyLedger = createCurrencyLedger();
    }
    return currencyLedger;
  }

  private static Map<CurrencyPair, Double> createCurrencyLedger() {
    Map<CurrencyPair, Double> retVal = new HashMap<>();
    for (Currency currencyOne : Currency.values()) {
      for (Currency currencyTwo : Currency.values()) {
        retVal.put(
            new CurrencyPair(currencyOne, currencyTwo),
            currencyOne == currencyTwo ? 1.0 : 100 * random.nextDouble());
      }
    }
    return retVal;
  }

  @AllArgsConstructor
  private static class CurrencyPair {
    private Currency currencyOne;
    private Currency currencyTwo;
  }
}
