package com.agilebank.controller;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
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

  @GetMapping("/currencyledger")
  public ResponseEntity<Map<CurrencyPair, BigDecimal>> getAllCurrencyExchangeRates() {
    return ResponseEntity.ok(currencyLedger.getCurrencyExchangeRates());
  }

  @GetMapping("/exchangerate")
  public ResponseEntity<BigDecimal> getCurrencyExchangeRate(
      @RequestParam(name = "currencyOne", required = false) Currency currencyOne,
      @RequestParam(name = "currencyTwo", required = false) Currency currencyTwo) throws MethodArgumentTypeMismatchException{
    return ResponseEntity.ok(
        currencyLedger.getCurrencyExchangeRates().get(new CurrencyPair(currencyOne, currencyTwo)));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  private ResponseEntity<String> badCurrencyProvided() {
    return new ResponseEntity<>(
        "Provided an invalid or non-existent currency as per ISO 4217; please check parameters.", HttpStatus.BAD_REQUEST);
  }
}
