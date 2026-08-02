package com.karthick.expenz.expenses.dto;

import java.util.List;

public record ExpenseSummaryDTO(
  long totalExpensesCount,
  long totalIncomesCount,
  double totalExpensesAmount,
  double totalIncomesAmount,
  double balanceAmount,
  List<PieDataItem> expensePieDataItems,
  List<PieDataItem> incomePieDataItems
) {}
