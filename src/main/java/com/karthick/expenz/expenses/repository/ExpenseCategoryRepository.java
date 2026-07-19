package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseCategoryRepository
  extends JpaRepository<ExpenseCategory, Long> {}
