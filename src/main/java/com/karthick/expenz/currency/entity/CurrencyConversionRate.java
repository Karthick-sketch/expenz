package com.karthick.expenz.currency.entity;

import com.karthick.expenz.users.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
  private String fromCurrency;

  @NonNull
  private String toCurrency;

  @NonNull
  private Double rate;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
