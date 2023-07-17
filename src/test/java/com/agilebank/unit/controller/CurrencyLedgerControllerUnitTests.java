package com.agilebank.unit.controller;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;
import static com.agilebank.util.TestUtils.TEST_EXCHANGE_RATES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.agilebank.controller.CurrencyLedgerController;
import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.util.exceptions.OneOfTwoCurrenciesMissingException;
import java.math.BigDecimal;
import java.util.Map;
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
  }

  @Test
  public void whenCurrencyLedgerReturnsTheFullExchangeRates_thenSoDoesTheController() {
    assertEquals(
        ResponseEntity.ok(TEST_EXCHANGE_RATES),
        currencyLedgerController.getCurrencyExchangeRate(null, null));
  }

  @Test
  public void whenRequestingASpecificExchangeRate_thenWeGetTheCorrectExchangeRate() {
    assertEquals(
        ResponseEntity.ok(
            Map.of(new CurrencyPair(Currency.USD, Currency.INR), BigDecimal.ONE)),
        currencyLedgerController.getCurrencyExchangeRate(Currency.USD, Currency.INR));
  }

  @Test(expected = OneOfTwoCurrenciesMissingException.class)
  public void
      whenProvidingCurrencyOneButNeglectingToProvideCurrencyTwo_thenOneOfTwoCurrenciesMissingExceptionIsThrown() {
    currencyLedgerController.getCurrencyExchangeRate(Currency.AFA, null);
  }

  @Test(expected = OneOfTwoCurrenciesMissingException.class)
  public void
      whenProvidingCurrencyTwoButNeglectingToProvideCurrencyOne_thenOneOfTwoCurrenciesMissingExceptionIsThrown() {
    currencyLedgerController.getCurrencyExchangeRate(null, Currency.AFA);
  }
}
