package com.karthick.expenz.expenses.service;

import com.karthick.expenz.expenses.dto.category.*;
import com.karthick.expenz.expenses.entity.ExpenseCategory;
import com.karthick.expenz.expenses.entity.ExpenseSubCategory;
import com.karthick.expenz.expenses.repository.ExpenseCategoryRepository;
import com.karthick.expenz.expenses.repository.ExpenseSubCategoryRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExpenseCategoryService {

  private final ExpenseCategoryRepository expenseCategoryRepository;
  private final ExpenseSubCategoryRepository expenseSubCategoryRepository;

  public List<ExpenseCategoryDTO> getAllCategories() {
    return expenseCategoryRepository
      .findAll()
      .stream()
      .map(c -> new ExpenseCategoryDTO(c.getId(), c.getName(), c.getIcon()))
      .toList();
  }

  public ExpenseCategoryDTO createCategory(
    ExpenseCategoryCreateDTO categoryCreateDTO
  ) {
    ExpenseCategory category = new ExpenseCategory();
    category.setName(categoryCreateDTO.getName());
    category.setIcon(categoryCreateDTO.getIcon());
    category = expenseCategoryRepository.save(category);
    return new ExpenseCategoryDTO(
      category.getId(),
      category.getName(),
      category.getIcon()
    );
  }

  public List<ExpenseSubCategoryDTO> getAllSubCategories(Long categoryId) {
    return expenseSubCategoryRepository
      .findByCategoryId(categoryId)
      .stream()
      .map(sc ->
        new ExpenseSubCategoryDTO(
          sc.getId(),
          sc.getName(),
          sc.getIcon(),
          sc.getCategory().getId()
        )
      )
      .toList();
  }

  public ExpenseSubCategoryDTO createSubCategory(
    ExpenseSubCategoryCreateDTO subCategoryCreateDTO
  ) {
    ExpenseSubCategory subCategory = new ExpenseSubCategory();
    subCategory.setName(subCategoryCreateDTO.getName());
    subCategory.setIcon(subCategoryCreateDTO.getIcon());
    subCategory.setCategory(
      expenseCategoryRepository
        .findById(subCategoryCreateDTO.getCategoryId())
        .orElseThrow()
    );
    subCategory = expenseSubCategoryRepository.save(subCategory);
    return new ExpenseSubCategoryDTO(
      subCategory.getId(),
      subCategory.getName(),
      subCategory.getIcon(),
      subCategory.getCategory().getId()
    );
  }
}
