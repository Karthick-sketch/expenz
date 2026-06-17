package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseRepository
  extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense>
{
  Optional<Expense> findByIdAndUserID(long id, long userId);

  List<Expense> findByUserId(long userId);
}
