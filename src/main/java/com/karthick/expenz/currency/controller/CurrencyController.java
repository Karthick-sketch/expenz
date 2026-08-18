package com.karthick.expenz.currency.controller;

import com.karthick.expenz.auth.UserSession;
import com.karthick.expenz.currency.dto.*;
import com.karthick.expenz.currency.service.CurrencyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currencies")
@AllArgsConstructor
public class CurrencyController {

  private CurrencyService currencyService;

  private UserSession userSession;

  private Long userId() {
    return userSession.getAuthenticatedUserId();
  }

  @PostMapping("/conversion-rates")
  public ResponseEntity<CurrencyConversionRateDTO> createCurrencyConversionRate(
    @RequestBody CurrencyConversionRateDTO currencyConversionRateDTO
  ) {
    return new ResponseEntity<>(
      currencyService.createCurrencyConversionRate(
        currencyConversionRateDTO,
        userId()
      ),
      HttpStatus.CREATED
    );
  }

  @GetMapping("/conversion-rates")
  public ResponseEntity<
    List<CurrencyConversionRateDTO>
  > getAllCurrencyConversionRates() {
    return ResponseEntity.ok(
      currencyService.getCurrencyConversionRates(userId())
    );
  }

  @PatchMapping("/conversion-rates")
  public ResponseEntity<CurrencyConversionRateDTO> updateCurrencyConversionRate(
    @RequestBody CurrencyConversionRateDTO currencyConversionRateDTO
  ) {
    return new ResponseEntity<>(
      currencyService.upsertCurrencyConversionRate(
        currencyConversionRateDTO,
        userId()
      ),
      HttpStatus.OK
    );
  }
}
