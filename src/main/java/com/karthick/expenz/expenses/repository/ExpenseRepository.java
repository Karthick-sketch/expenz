package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.Expense;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  List<Expense> findByUserId(long userId);

  @Query(
    value = "SELECT * FROM expenses WHERE EXTRACT(MONTH FROM date_added) = ?1 AND EXTRACT(YEAR FROM date_added) = ?2 AND user_id = ?3",
    nativeQuery = true
  )
  List<Expense> findExpensesByMonthAndYear(int month, int year, long userId);

  @Query(
    value = "SELECT * FROM expenses WHERE is_it_income = ?1 AND EXTRACT(MONTH FROM date_added) = ?2 AND EXTRACT(YEAR FROM date_added) = ?3 AND user_id = ?4",
    nativeQuery = true
  )
  List<Expense> findExpensesByTypeMonthAndYear(
    boolean isItIncome,
    int month,
    int year,
    long userId
  );
}
