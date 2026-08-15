package com.karthick.expenz.currency.dto;

public record CurrencyConversionRateDTO(
  String fromCurrency,
  String toCurrency,
  Double rate
) {}
