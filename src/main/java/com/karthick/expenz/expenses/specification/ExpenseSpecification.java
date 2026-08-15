package com.karthick.expenz.expenses.specification;

import com.karthick.expenz.enums.ExpenseType;
import com.karthick.expenz.expenses.entity.Expense;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecification {

  public static Specification<Expense> withUserId(long userId) {
    return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  public static Specification<Expense> withExpenseType(ExpenseType type) {
    if (type == null || type == ExpenseType.ALL) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) ->
      cb.equal(root.get("income"), ExpenseType.INCOME.equals(type));
  }

  public static Specification<Expense> withFromDate(LocalDate fromDate) {
    if (fromDate == null) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) ->
      cb.greaterThanOrEqualTo(root.get("dateAdded"), fromDate);
  }

  public static Specification<Expense> withToDate(LocalDate toDate) {
    if (toDate == null) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) ->
      cb.lessThanOrEqualTo(root.get("dateAdded"), toDate);
  }

  public static Specification<Expense> withCategory(Long categoryId) {
    if (categoryId == null || categoryId <= 0) {
      return (root, query, cb) -> cb.conjunction();
    }

    return (root, query, cb) ->
      cb.equal(root.get("subCategory").get("category").get("id"), categoryId);
  }

  public static Specification<Expense> withSubCategory(Long subCategoryId) {
    if (subCategoryId == null || subCategoryId <= 0) {
      return (root, query, cb) -> cb.conjunction();
    }
    return (root, query, cb) ->
      cb.equal(root.get("subCategory").get("id"), subCategoryId);
  }

  public static Specification<Expense> withSearchTerm(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      return (root, query, cb) -> cb.conjunction();
    }

    String pattern = "%" + searchTerm.toLowerCase() + "%";

    return (root, query, cb) -> {
      Predicate titlePredicate = cb.like(cb.lower(root.get("title")), pattern);
      Predicate descriptionPredicate = cb.like(
        cb.lower(root.get("description")),
        pattern
      );
      Predicate subCategoryNamePredicate = cb.like(
        cb.lower(root.get("subCategory").get("name")),
        pattern
      );

      return cb.or(
        titlePredicate,
        descriptionPredicate,
        subCategoryNamePredicate
      );
    };
  }

  public static Specification<Expense> orderByDateAddedDesc() {
    return (root, query, cb) -> {
      query.orderBy(cb.desc(root.get("dateAdded")));
      return cb.conjunction();
    };
  }
}
