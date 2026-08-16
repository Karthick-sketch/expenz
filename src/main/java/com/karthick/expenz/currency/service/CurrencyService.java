package com.karthick.expenz.currency.service;

import com.karthick.expenz.currency.dto.CurrencyConversionRateDTO;
import com.karthick.expenz.currency.entity.CurrencyConversionRate;
import com.karthick.expenz.currency.repository.CurrencyConversionRateRepository;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrencyService {

  private CurrencyConversionRateRepository currencyConversionRateRepository;

  private UserService userService;

  public CurrencyConversionRateDTO createCurrencyConversionRate(
    CurrencyConversionRateDTO currencyConversionRateDTO,
    long userId
  ) {
    CurrencyConversionRate currencyConversionRate = toCurrencyConversionRate(
      currencyConversionRateDTO,
      userId
    );
    try {
      return toCurrencyConversionRateDTO(
        currencyConversionRateRepository.save(currencyConversionRate)
      );
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public CurrencyConversionRate findCurrencyConversionRateById(
    long id,
    long userId
  ) {
    return currencyConversionRateRepository
      .findByIdAndUserId(id, userId)
      .orElseThrow(() ->
        new EntityNotFoundException("Currency conversion rate not found")
      );
  }

  public CurrencyConversionRate findCurrencyConversionRate(
    String fromCurrency,
    String toCurrency,
    long userId
  ) {
    return currencyConversionRateRepository
      .findByFromCurrencyAndToCurrencyAndUserId(
        fromCurrency,
        toCurrency,
        userId
      )
      .orElseThrow(() ->
        new EntityNotFoundException("Currency conversion rate not found")
      );
  }

  public CurrencyConversionRateDTO getCurrencyConversionRate(
    String fromCurrency,
    String toCurrency,
    long userId
  ) {
    try {
      return toCurrencyConversionRateDTO(
        findCurrencyConversionRate(fromCurrency, toCurrency, userId)
      );
    } catch (EntityNotFoundException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public CurrencyConversionRateDTO updateCurrencyConversionRate(
    long id,
    CurrencyConversionRateDTO currencyConversionRateDTO,
    long userId
  ) {
    CurrencyConversionRate currencyConversionRate =
      findCurrencyConversionRateById(id, userId);
    currencyConversionRate.setFromCurrency(
      currencyConversionRateDTO.fromCurrency()
    );
    currencyConversionRate.setToCurrency(
      currencyConversionRateDTO.toCurrency()
    );
    currencyConversionRate.setRate(currencyConversionRateDTO.rate());
    try {
      return toCurrencyConversionRateDTO(
        currencyConversionRateRepository.save(currencyConversionRate)
      );
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  private CurrencyConversionRate toCurrencyConversionRate(
    CurrencyConversionRateDTO currencyConversionRateDTO,
    Long userId
  ) {
    return new CurrencyConversionRate(
      currencyConversionRateDTO.fromCurrency(),
      currencyConversionRateDTO.toCurrency(),
      currencyConversionRateDTO.rate(),
      LocalDate.now(),
      userService.findUser(userId)
    );
  }

  private CurrencyConversionRateDTO toCurrencyConversionRateDTO(
    CurrencyConversionRate currencyConversionRate
  ) {
    return new CurrencyConversionRateDTO(
      currencyConversionRate.getFromCurrency(),
      currencyConversionRate.getToCurrency(),
      currencyConversionRate.getRate()
    );
  }
}
