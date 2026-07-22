package com.karthick.expenz.expenses.dto;

import java.util.List;

public record ExpenseListDTO(
  long totalExpensesCount,
  long totalIncomesCount,
  double totalExpensesAmount,
  double totalIncomesAmount,
  double balanceAmount,
  List<ExpenseDTO> expenses
) {}
