package com.agilebank.controller;

import static com.agilebank.model.currency.CurrencyLedger.CurrencyPair;

import com.agilebank.model.currency.Currency;
import com.agilebank.model.currency.CurrencyLedger;
import com.agilebank.util.logger.Logged;
import com.agilebank.util.exceptions.OneOfTwoCurrenciesMissingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
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
@Tag(name = "4. Currency Ledger API")
@Logged
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
  @Operation(summary = "Get the full list of exchange rates or a specific one")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Exchange rate returned.",
            content = {
              @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/ExchangeRate"))
            }),
        @ApiResponse(
                responseCode = "400",
                description = "Bad pagination parameters specified.",
                content = @Content
        ),      
        @ApiResponse(
                responseCode = "401",
                description = "Unauthenticated.",
                content = @Content
        )
      })
  @GetMapping("/exchangerate")
  public ResponseEntity<Map<CurrencyPair, BigDecimal>> getCurrencyExchangeRate(
          @RequestParam(name = "currencyOne", required = false) Currency currencyOne,
          @RequestParam(name = "currencyTwo", required = false) Currency currencyTwo,
          @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) Integer page, 
      @RequestParam(name = "page_size", required = false, defaultValue = "10") @Min(1) Integer pageSize)
        throws MethodArgumentTypeMismatchException, OneOfTwoCurrenciesMissingException {
    if (exactlyOneOfCurrenciesIsNull(currencyOne, currencyTwo)) {
      throw new OneOfTwoCurrenciesMissingException();
    }
    if(currencyOne != null && currencyTwo != null){
      CurrencyPair currencyPair = CurrencyPair.of(currencyOne, currencyTwo);
      return ResponseEntity.ok(Map.of(currencyPair, currencyLedger.getCurrencyExchangeRates().get(currencyPair)));
    } else { // Both currencies null, give me everything
      return ResponseEntity.ok( (page != null && pageSize != null) ? currencyLedger.getCurrencyExchangeRates(page, pageSize) : 
              currencyLedger.getCurrencyExchangeRates());
    }
    
  }

  private boolean exactlyOneOfCurrenciesIsNull(Object one, Object two) {
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
