package com.karthick.expenz.expenses.specification;

import com.karthick.expenz.expenses.entity.Expense;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecification {

  public static Specification<Expense> withUserId(long userId) {
    return (root, query, cb) -> cb.equal(root.get("user_id"), userId);
  }

  public static Specification<Expense> withExpenseType(Boolean expensType) {
    if (expensType == null) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) -> cb.equal(root.get("is_income"), expensType);
  }

  public static Specification<Expense> withMonth(Integer month) {
    if (month == null) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) ->
      cb.equal(
        cb.function(
          "EXTRACT",
          Integer.class,
          cb.literal("MONTH FROM"),
          root.get("date_added")
        ),
        month
      );
  }

  public static Specification<Expense> withYear(Integer year) {
    if (year == null) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) ->
      cb.equal(
        cb.function(
          "EXTRACT",
          Integer.class,
          cb.literal("YEAR FROM"),
          root.get("date_added")
        ),
        year
      );
  }
}
