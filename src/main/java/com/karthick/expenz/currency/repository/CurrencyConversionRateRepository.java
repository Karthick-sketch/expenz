package com.karthick.expenz.currency.repository;

import com.karthick.expenz.currency.entity.CurrencyConversionRate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyConversionRateRepository
  extends
    JpaRepository<CurrencyConversionRate, Long>,
    JpaSpecificationExecutor<CurrencyConversionRate>
{
  List<CurrencyConversionRate> findByUserId(long userId);

  Optional<CurrencyConversionRate> findByFromCurrencyAndToCurrencyAndUserId(
    String fromCurrency,
    String toCurrency,
    Long userId
  );
}
