package com.karthick.expenz.expenses.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

  private Long id;
  private double amount;
  private String title;
  private String description;
  private Long categoryId;
  private Long subCategoryId;
  private boolean income;
  private LocalDate dateAdded;
  private Long expenseGroupId;
}
