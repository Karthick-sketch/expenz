package com.karthick.expenz.expenses.dto;

public record ExpenseGroupListDTO(
  long id,
  String title,
  String description,
  long expenseCount,
  long incomeCount,
  double totalExpensesAmount,
  double totalIncomesAmount,
  double balanceAmount
) {}
