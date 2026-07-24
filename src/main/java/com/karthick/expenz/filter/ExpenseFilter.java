package com.karthick.expenz.filter;

import com.karthick.expenz.enums.ExpenseDuration;
import com.karthick.expenz.enums.ExpenseType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFilter {

  private ExpenseType type;
  private Long subCategoryId;
  private ExpenseDuration duration;
  private LocalDate fromDate;
  private LocalDate toDate;
  private String searchTerm;
}
