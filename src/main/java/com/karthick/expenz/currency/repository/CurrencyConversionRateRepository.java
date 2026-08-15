package com.karthick.expenz.currency.repository;

import com.karthick.expenz.currency.entity.CurrencyConversionRate;
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
  Optional<CurrencyConversionRate> findByIdAndUserId(long id, long userId);

  Optional<CurrencyConversionRate> findByFromCurrencyAndToCurrencyAndUserId(
    String fromCurrency,
    String toCurrency,
    Long userId
  );
}
