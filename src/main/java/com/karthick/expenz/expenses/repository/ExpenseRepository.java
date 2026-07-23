package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository
  extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense>
{
  Optional<Expense> findByIdAndUserId(long id, long userId);

  List<Expense> findByUserId(long userId);

  /* Dashboard */
  Long countByIncomeAndUserId(boolean income, Long userId);

  /* Expense Group */
  @Query(
    "SELECT COUNT(e) FROM Expense e WHERE e.income = :income AND e.expenseGroup.id = :expenseGroupId"
  )
  Long countTotalExpensesInGroup(
    @Param("income") boolean income,
    @Param("expenseGroupId") Long expenseGroupId
  );

  @Query(
    "SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.user.id = :userId AND e.income = :income"
  )
  Double getTotalExpenses(
    @Param("userId") Long userId,
    @Param("income") boolean income
  );

  @Query(
    "SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.expenseGroup.id = :groupId AND e.income = :income"
  )
  Double getTotalExpensesInGroup(
    @Param("groupId") Long groupId,
    @Param("income") boolean income
  );

  @Query(
    "SELECT e FROM Expense e WHERE e.user.id = :userId ORDER BY e.dateAdded DESC LIMIT 5"
  )
  List<Expense> getRecentExpenses(@Param("userId") Long userId);

  List<Expense> findByExpenseGroupId(Long expenseGroupId);
}
