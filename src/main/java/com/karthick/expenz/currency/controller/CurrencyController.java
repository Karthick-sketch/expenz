package com.karthick.expenz.currency.controller;

import com.karthick.expenz.auth.UserSession;
import com.karthick.expenz.currency.dto.*;
import com.karthick.expenz.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currencies")
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
  public ResponseEntity<CurrencyConversionRateDTO> getCurrencyConversionRate(
    @RequestBody CurrencyConversionDTO currencyConversionDTO
  ) {
    return new ResponseEntity<>(
      currencyService.getCurrencyConversionRate(
        currencyConversionDTO.fromCurrency(),
        currencyConversionDTO.toCurrency(),
        userId()
      ),
      HttpStatus.OK
    );
  }

  @PatchMapping("/conversion-rates/{id}")
  public ResponseEntity<CurrencyConversionRateDTO> updateCurrencyConversionRate(
    @PathVariable long id,
    @RequestBody CurrencyConversionRateDTO currencyConversionRateDTO
  ) {
    return new ResponseEntity<>(
      currencyService.updateCurrencyConversionRate(
        id,
        currencyConversionRateDTO,
        userId()
      ),
      HttpStatus.OK
    );
  }
}
