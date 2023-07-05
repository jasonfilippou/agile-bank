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

/**
 * {@link RestController} for {@link Currency} exchange rates. 
 * 
 * @author jason 
 * 
 * @see CurrencyLedger
 * @see Currency
 */
@RestController
@RequestMapping("/bankapi")
@RequiredArgsConstructor
public class CurrencyLedgerController {

  private final CurrencyLedger currencyLedger;

  /**
   * GET endpoint for {@link Currency} exchange rates.
   * @param currencyOne Optional parameter for the first {@link Currency} to find the exchange rate of with another {@link Currency}.
   * @param currencyTwo Optional parameter for the second {@link Currency} to find the exchange rate of with another {@link Currency}.
   * @return A {@link ResponseEntity} over a {@link Map} from pairs of {@link Currency} instances to {@link BigDecimal}s 
   * representing the cost of purchasing one unit of {@literal currencyTwo} with units of {@literal currencyOne}.
   * @throws MethodArgumentTypeMismatchException If the user provides a parameter that does not match one a valid {@link Currency} value.
   * @throws OneOfTwoCurrenciesMissingException If exactly one of {@literal currencyOne} or {@literal currencyTwo} are {@literal null}.
   */
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
