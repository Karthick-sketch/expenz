package com.karthick.expenz.expenses.util;

import com.karthick.expenz.enums.ExpenseDuration;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.PieDataItem;
import com.karthick.expenz.expenses.dto.category.ExpenseCategoryDTO;
import com.karthick.expenz.filter.ExpenseFilter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpenseUtils {

  public static List<PieDataItem> calculatePieData(
    List<ExpenseDTO> expenses,
    List<ExpenseCategoryDTO> categories,
    boolean income
  ) {
    List<PieDataItem> pieDataItems = new ArrayList<>();

    for (ExpenseDTO exp : expenses) {
      Optional<ExpenseCategoryDTO> category = categories
        .stream()
        .filter(cat -> cat.getId() == exp.getCategoryId())
        .findFirst();
      category.ifPresent(cat -> {
        if (exp.getIncome() == income) {
          Optional<PieDataItem> existingCategory = pieDataItems
            .stream()
            .filter(item -> item.getName() == cat.getName())
            .findFirst();
          if (existingCategory.isPresent()) {
            PieDataItem ec = existingCategory.get();
            ec.setValue(ec.getValue().add(exp.getAmount()));
          } else {
            pieDataItems.add(
              new PieDataItem(cat.getName(), exp.getAmount(), cat.getColorHex())
            );
          }
        }
      });
    }

    return pieDataItems;
  }

  public static void calculateDateRange(ExpenseFilter filter) {
    if (filter.getDuration() == null) {
      filter.setDuration(ExpenseDuration.ALL_TIME);
    }

    LocalDate fromDate = filter.getFromDate();
    LocalDate toDate = filter.getToDate();
    switch (filter.getDuration()) {
      case ALL_TIME:
        fromDate = null;
        toDate = null;
        break;
      case THIS_WEEK:
        fromDate = LocalDate.now().with(DayOfWeek.MONDAY);
        toDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        break;
      case LAST_WEEK:
        fromDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        toDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        break;
      case THIS_MONTH:
        fromDate = LocalDate.now().withDayOfMonth(1);
        toDate = LocalDate.now().withDayOfMonth(
          LocalDate.now().lengthOfMonth()
        );
        break;
      case LAST_MONTH:
        fromDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        toDate = LocalDate.now().withDayOfMonth(
          LocalDate.now().lengthOfMonth()
        );
        break;
      case THIS_YEAR:
        fromDate = LocalDate.now().withDayOfYear(1);
        toDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        break;
      case LAST_YEAR:
        fromDate = LocalDate.now().minusYears(1).withDayOfYear(1);
        toDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        break;
      case DATE_RANGE:
        break;
    }
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
  }
}
