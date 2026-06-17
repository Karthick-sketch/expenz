package com.karthick.expenz.expenses.dto;

public record ExpenseUpdateDTO(
  double amount,
  String title,
  String description,
  String category,
  boolean isIncome
) {}
