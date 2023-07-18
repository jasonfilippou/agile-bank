package com.agilebank.unit.controller;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;
import static com.agilebank.util.TestUtils.TEST_EXCHANGE_RATES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.agilebank.controller.CurrencyLedgerController;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.util.exceptions.OneOfTwoCurrenciesMissingException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyLedgerControllerUnitTests {

  @InjectMocks private CurrencyLedgerController currencyLedgerController;

  @Mock private CurrencyLedger currencyLedger = new CurrencyLedger();

  @Before
  public void setUp() {
    when(currencyLedger.getCurrencyExchangeRates()).thenReturn(TEST_EXCHANGE_RATES);
    when(currencyLedger.getCurrencyExchangeRates(any(Integer.class), any(Integer.class))).thenAnswer(invocationOnMock -> {
      Integer slice = invocationOnMock.getArgument(0);
      Integer sliceSize = invocationOnMock.getArgument(1);
      return TEST_EXCHANGE_RATES.entrySet().stream().skip((long) slice * sliceSize)
              .limit(sliceSize).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    });
  }

  @Test
  public void whenCurrencyLedgerReturnsTheFullExchangeRates_thenSoDoesTheController() {
    assertEquals(
        ResponseEntity.ok(TEST_EXCHANGE_RATES),
        currencyLedgerController.getCurrencyExchangeRate(null, null, null, null));
  }
  
  @Test
  public void whenCurrencyLedgerPaginatesTheExchangeRates_thenExpectedSliceIsReturned(){
    // There are 16 = 3 * 5 + 1 exchange different rates in the testing instance
    final int pageSize = 5;
    assertEquals(ResponseEntity.ok(TEST_EXCHANGE_RATES.entrySet().stream().skip(0).limit(pageSize).collect(Collectors.toMap(Map.Entry::getKey,
            Map.Entry::getValue))), currencyLedgerController.getCurrencyExchangeRate(null, null, 0, pageSize));
    assertEquals(ResponseEntity.ok(TEST_EXCHANGE_RATES.entrySet().stream().skip(pageSize).limit(pageSize).collect(Collectors.toMap(Map.Entry::getKey,
            Map.Entry::getValue))), currencyLedgerController.getCurrencyExchangeRate(null, null, 1, pageSize));
    assertEquals(ResponseEntity.ok(TEST_EXCHANGE_RATES.entrySet().stream().skip(2 * pageSize).limit(pageSize).collect(Collectors.toMap(Map.Entry::getKey,
            Map.Entry::getValue))), currencyLedgerController.getCurrencyExchangeRate(null, null, 2, pageSize));
    assertEquals(ResponseEntity.ok(TEST_EXCHANGE_RATES.entrySet().stream().skip(3 * pageSize).limit(pageSize).collect(Collectors.toMap(Map.Entry::getKey,
            Map.Entry::getValue))), currencyLedgerController.getCurrencyExchangeRate(null, null, 3, pageSize));


  }

  @Test
  public void whenRequestingASpecificExchangeRate_thenWeGetTheCorrectExchangeRate() {
    assertEquals(
        ResponseEntity.ok(
            Map.of(new CurrencyPair(Currency.USD, Currency.INR), BigDecimal.ONE)),
        currencyLedgerController.getCurrencyExchangeRate(Currency.USD, Currency.INR, null, null));
  }

  @Test(expected = OneOfTwoCurrenciesMissingException.class)
  public void
      whenProvidingCurrencyOneButNeglectingToProvideCurrencyTwo_thenOneOfTwoCurrenciesMissingExceptionIsThrown() {
    currencyLedgerController.getCurrencyExchangeRate(Currency.AFA, null, null, null);
  }

  @Test(expected = OneOfTwoCurrenciesMissingException.class)
  public void
      whenProvidingCurrencyTwoButNeglectingToProvideCurrencyOne_thenOneOfTwoCurrenciesMissingExceptionIsThrown() {
    currencyLedgerController.getCurrencyExchangeRate(null, Currency.AFA, null, null);
  }
}
