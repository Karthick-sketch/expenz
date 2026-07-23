package com.karthick.expenz.expenses.service;

import com.karthick.expenz.exception.EntityNotFoundException;
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
      .map(this::toExpenseCategoryDTO)
      .toList();
  }

  public ExpenseCategory getCategory(Long id) {
    return expenseCategoryRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException(id, ExpenseCategory.class)
      );
  }

  public ExpenseCategoryDTO createCategory(
    ExpenseCategoryCreateDTO categoryCreateDTO
  ) {
    ExpenseCategory category = new ExpenseCategory();
    category.setName(categoryCreateDTO.getName());
    category.setIcon(categoryCreateDTO.getIcon());
    category = expenseCategoryRepository.save(category);
    return toExpenseCategoryDTO(category);
  }

  public List<ExpenseSubCategoryDTO> getAllSubCategories(Long categoryId) {
    return expenseSubCategoryRepository
      .findByCategoryId(categoryId)
      .stream()
      .map(this::toExpenseSubCategoryDTO)
      .toList();
  }

  public ExpenseSubCategory getSubCategory(Long id) {
    return expenseSubCategoryRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException(id, ExpenseSubCategory.class)
      );
  }

  public ExpenseSubCategoryDTO createSubCategory(
    ExpenseSubCategoryCreateDTO subCategoryCreateDTO
  ) {
    ExpenseSubCategory subCategory = new ExpenseSubCategory();
    subCategory.setName(subCategoryCreateDTO.getName());
    subCategory.setIcon(subCategoryCreateDTO.getIcon());
    subCategory.setCategory(getCategory(subCategoryCreateDTO.getCategoryId()));
    subCategory = expenseSubCategoryRepository.save(subCategory);
    return toExpenseSubCategoryDTO(subCategory);
  }

  private ExpenseCategoryDTO toExpenseCategoryDTO(ExpenseCategory category) {
    return new ExpenseCategoryDTO(
      category.getId(),
      category.getName(),
      category.getIcon(),
      category.getColorHex()
    );
  }

  private ExpenseSubCategoryDTO toExpenseSubCategoryDTO(
    ExpenseSubCategory subCategory
  ) {
    return new ExpenseSubCategoryDTO(
      subCategory.getId(),
      subCategory.getName(),
      subCategory.getIcon(),
      subCategory.getCategory().getId()
    );
  }
}
