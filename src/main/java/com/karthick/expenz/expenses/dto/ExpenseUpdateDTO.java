package com.karthick.expenz.expenses.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseUpdateDTO(
  BigDecimal amount,
  String title,
  String description,
  Long categoryId,
  Long subCategoryId,
  Boolean income,
  LocalDate dateAdded,
  Long expenseGroupId
) {}
