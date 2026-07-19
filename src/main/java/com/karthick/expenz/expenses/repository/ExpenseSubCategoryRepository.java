package com.karthick.expenz.expenses.repository;

import com.karthick.expenz.expenses.entity.ExpenseSubCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseSubCategoryRepository
  extends JpaRepository<ExpenseSubCategory, Long>
{
  List<ExpenseSubCategory> findByCategoryId(Long categoryId);
  Optional<ExpenseSubCategory> findByName(String name);
}
