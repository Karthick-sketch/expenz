package com.karthick.expenz.expenses.dto.group;

import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.PieDataItem;
import java.math.BigDecimal;
import java.util.List;

public record ExpenseGroupDTO(
  Long id,
  String title,
  String description,
  Long totalExpensesCount,
  Long totalIncomesCount,
  BigDecimal totalExpensesAmount,
  BigDecimal totalIncomesAmount,
  BigDecimal balanceAmount,
  List<ExpenseDTO> expenses,
  List<PieDataItem> expensePieDataItems,
  List<PieDataItem> incomePieDataItems
) {}
