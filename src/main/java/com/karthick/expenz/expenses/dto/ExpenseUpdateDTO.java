package com.karthick.expenz.expenses.dto;

import java.time.LocalDate;

public record ExpenseUpdateDTO(
  double amount,
  String title,
  String description,
  String category,
  boolean income,
  LocalDate dateAdded,
  Long expenseGroupId
) {}
