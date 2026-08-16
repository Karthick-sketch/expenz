package com.karthick.expenz.expenses.dto;

import java.math.BigDecimal;
import java.util.List;

public record ExpenseSummaryDTO(
  Long totalExpensesCount,
  Long totalIncomesCount,
  BigDecimal totalExpensesAmount,
  BigDecimal totalIncomesAmount,
  BigDecimal balanceAmount,
  List<PieDataItem> expensePieDataItems,
  List<PieDataItem> incomePieDataItems
) {}
