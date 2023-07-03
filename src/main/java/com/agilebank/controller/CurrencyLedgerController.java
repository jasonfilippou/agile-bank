package com.agilebank.controller;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.util.exceptions.OneOfTwoCurrenciesMissingException;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
public class CurrencyLedgerController {

  private final CurrencyLedger currencyLedger;

  @GetMapping("/exchangerate")
  public ResponseEntity<Map<CurrencyPair, BigDecimal>> getCurrencyExchangeRate(
      @RequestParam(name = "currencyOne", required = false) Currency currencyOne,
      @RequestParam(name = "currencyTwo", required = false) Currency currencyTwo)
      throws MethodArgumentTypeMismatchException, OneOfTwoCurrenciesMissingException {
    if (currencyOne == null && currencyTwo == null) {
      return ResponseEntity.ok(currencyLedger.getCurrencyExchangeRates());
    } else if (exactlyOneOfParamsIsNull(currencyOne, currencyTwo)) {
      throw new OneOfTwoCurrenciesMissingException();
    }
    CurrencyPair currencyPair = new CurrencyPair(currencyOne, currencyTwo);
    return ResponseEntity.ok(
        Map.of(currencyPair, currencyLedger.getCurrencyExchangeRates().get(currencyPair)));
  }

  private boolean exactlyOneOfParamsIsNull(Object one, Object two) {
    return (one == null && two != null) || (one != null && two == null);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  private ResponseEntity<String> badCurrencyProvided() {
    return new ResponseEntity<>(
        "Provided an invalid or non-existent currency as per ISO 4217; please check parameters.",
        HttpStatus.BAD_REQUEST);
  }
}
