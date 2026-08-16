package com.karthick.expenz.currency.entity;

import com.karthick.expenz.users.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "currency_conversion_rates")
public class CurrencyConversionRate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NonNull
  @Column(name = "from_currency")
  private String fromCurrency;

  @NonNull
  @Column(name = "to_currency")
  private String toCurrency;

  @NonNull
  @Column(name = "rate")
  private BigDecimal rate;

  @NonNull
  @Column(name = "last_updated")
  private LocalDate lastUpdated;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
