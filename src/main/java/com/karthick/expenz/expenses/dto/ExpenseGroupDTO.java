package com.karthick.expenz.expenses.dto;

import java.util.List;

public record ExpenseGroupDTO(
  long id,
  String title,
  String description,
  long totalExpensesCount,
  long totalIncomesCount,
  double totalExpensesAmount,
  double totalIncomesAmount,
  double balanceAmount,
  List<ExpenseDTO> expenses
) {}
