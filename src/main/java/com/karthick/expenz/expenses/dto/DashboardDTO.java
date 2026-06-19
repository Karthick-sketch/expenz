package com.karthick.expenz.expenses.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

  private Double balance;
  private Double totalExpenses;
  private Double totalIncome;
  private Long totalExpenseCount;
  private Long totalIncomeCount;
  private List<ExpenseDTO> recentExpenses;
}
