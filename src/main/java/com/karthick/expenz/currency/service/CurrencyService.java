package com.karthick.expenz.currency.service;

import com.karthick.expenz.currency.dto.CurrencyConversionRateDTO;
import com.karthick.expenz.currency.entity.CurrencyConversionRate;
import com.karthick.expenz.currency.repository.CurrencyConversionRateRepository;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

  public List<CurrencyConversionRateDTO> getCurrencyConversionRates(
    long userId
  ) {
    return currencyConversionRateRepository
      .findByUserId(userId)
      .stream()
      .map(this::toCurrencyConversionRateDTO)
      .toList();
  }

  public Optional<CurrencyConversionRate> findCurrencyConversionRate(
    String fromCurrency,
    String toCurrency,
    long userId
  ) {
    return currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(
      fromCurrency,
      toCurrency,
      userId
    );
  }

  public CurrencyConversionRateDTO getCurrencyConversionRate(
    String fromCurrency,
    String toCurrency,
    long userId
  ) {
    try {
      return toCurrencyConversionRateDTO(
        findCurrencyConversionRate(
          fromCurrency,
          toCurrency,
          userId
        ).orElseThrow(() ->
          new EntityNotFoundException("Currency conversion rate not found")
        )
      );
    } catch (EntityNotFoundException ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public CurrencyConversionRateDTO upsertCurrencyConversionRate(
    CurrencyConversionRateDTO currencyConversionRateDTO,
    long userId
  ) {
    Optional<CurrencyConversionRate> rateOptional = findCurrencyConversionRate(
      currencyConversionRateDTO.fromCurrency(),
      currencyConversionRateDTO.toCurrency(),
      userId
    );

    if (rateOptional.isEmpty()) {
      return createCurrencyConversionRate(currencyConversionRateDTO, userId);
    }

    CurrencyConversionRate conversionRate = rateOptional.get();
    conversionRate.setRate(currencyConversionRateDTO.rate());
    conversionRate.setLastUpdated(LocalDate.now());
    try {
      return toCurrencyConversionRateDTO(
        currencyConversionRateRepository.save(conversionRate)
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
