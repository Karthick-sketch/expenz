package com.karthick.expenz.expenses.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

  private long id;
  private double amount;
  private String title;
  private String description;
  private String category;
  private boolean isIncome;
  private Date dateAdded;
}
