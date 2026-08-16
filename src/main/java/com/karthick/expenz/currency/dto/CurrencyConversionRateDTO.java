package com.karthick.expenz.currency.dto;

import java.math.BigDecimal;

public record CurrencyConversionRateDTO(
  String fromCurrency,
  String toCurrency,
  BigDecimal rate
) {}
