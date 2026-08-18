package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.ExpenseSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseSubCategoryRepository
  extends JpaRepository<ExpenseSubCategory, Long> {}
