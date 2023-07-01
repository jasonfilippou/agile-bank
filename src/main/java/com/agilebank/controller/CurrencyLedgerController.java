package com.agilebank.controller;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("/bankapi")
public class CurrencyLedgerController {

  private final CurrencyLedger currencyLedger;

  @Autowired
  public CurrencyLedgerController(CurrencyLedger currencyLedger) {
    this.currencyLedger = currencyLedger;
  }

  @GetMapping("/currencyledger")
  public ResponseEntity<Map<CurrencyPair, BigDecimal>> getAllCurrencyExchangeRates() {
    return ResponseEntity.ok(currencyLedger.getCurrencyExchangeRates());
  }

  @GetMapping("/exchangerate")
  public ResponseEntity<BigDecimal> getCurrencyExchangeRate(
      @RequestParam(name = "currencyOne") Currency currencyOne,
      @RequestParam(name = "currencyTwo") Currency currencyTwo) throws MethodArgumentTypeMismatchException{
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
