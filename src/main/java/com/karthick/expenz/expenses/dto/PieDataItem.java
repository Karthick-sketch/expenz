package com.karthick.expenz.expenses.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PieDataItem {

  private String name;
  private BigDecimal value;
  private String color;
}
