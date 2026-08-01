package com.karthick.expenz.expenses.dto;

public record ExpenseSummaryDTO(
  long totalExpensesCount,
  long totalIncomesCount,
  double totalExpensesAmount,
  double totalIncomesAmount,
  double balanceAmount
) {}
