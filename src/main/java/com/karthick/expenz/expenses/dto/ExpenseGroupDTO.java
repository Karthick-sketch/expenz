package com.karthick.expenz.expenses.dto;

public record ExpenseGroupDTO(
  Long id,
  String title,
  String description,
  Long expenseCount,
  Long incomeCount,
  Double totalExpensesAmount,
  Double totalIncomesAmount,
  Double balanceAmount
) {}
