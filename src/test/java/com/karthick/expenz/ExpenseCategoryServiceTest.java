package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.category.*;
import com.karthick.expenz.expenses.entity.ExpenseCategory;
import com.karthick.expenz.expenses.entity.ExpenseSubCategory;
import com.karthick.expenz.expenses.repository.ExpenseCategoryRepository;
import com.karthick.expenz.expenses.repository.ExpenseSubCategoryRepository;
import com.karthick.expenz.expenses.service.ExpenseCategoryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExpenseCategoryServiceTest {

  @Mock
  private ExpenseCategoryRepository expenseCategoryRepository;

  @Mock
  private ExpenseSubCategoryRepository expenseSubCategoryRepository;

  @InjectMocks
  private ExpenseCategoryService expenseCategoryService;

  private ExpenseCategory getTestCategory() {
    ExpenseCategory category = new ExpenseCategory();
    category.setId(1L);
    category.setName("Food");
    category.setIcon("food-icon");
    category.setColorHex("#FF0000");
    return category;
  }

  private ExpenseSubCategory getTestSubCategory() {
    ExpenseSubCategory subCategory = new ExpenseSubCategory();
    subCategory.setId(1L);
    subCategory.setName("Groceries");
    subCategory.setIcon("grocery-icon");
    subCategory.setCategory(getTestCategory());
    return subCategory;
  }

  @Test
  public void testGetAllCategories() {
    ExpenseCategory category1 = getTestCategory();
    ExpenseCategory category2 = new ExpenseCategory();
    category2.setId(2L);
    category2.setName("Transport");
    category2.setIcon("transport-icon");
    category2.setColorHex("#00FF00");

    when(expenseCategoryRepository.findAll()).thenReturn(
      List.of(category1, category2)
    );

    List<ExpenseCategoryDTO> result = expenseCategoryService.getAllCategories();

    assertEquals(2, result.size());
    assertEquals(category1.getId(), result.get(0).getId());
    assertEquals(category1.getName(), result.get(0).getName());
    assertEquals(category1.getIcon(), result.get(0).getIcon());
    assertEquals(category1.getColorHex(), result.get(0).getColorHex());

    assertEquals(category2.getId(), result.get(1).getId());
    assertEquals(category2.getName(), result.get(1).getName());
  }

  @Test
  public void testGetAllCategories_Empty() {
    when(expenseCategoryRepository.findAll()).thenReturn(List.of());

    List<ExpenseCategoryDTO> result = expenseCategoryService.getAllCategories();

    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetCategory_Success() {
    ExpenseCategory category = getTestCategory();
    when(expenseCategoryRepository.findById(1L)).thenReturn(
      Optional.of(category)
    );

    ExpenseCategory result = expenseCategoryService.getCategory(1L);

    assertNotNull(result);
    assertEquals(category.getId(), result.getId());
    assertEquals(category.getName(), result.getName());
  }

  @Test
  public void testGetCategory_NotFound() {
    when(expenseCategoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      expenseCategoryService.getCategory(99L)
    );
  }

  @Test
  public void testCreateCategory() {
    ExpenseCategory category = getTestCategory();
    ExpenseCategoryCreateDTO createDTO = new ExpenseCategoryCreateDTO(
      "Food",
      "food-icon",
      "#FF0000"
    );

    when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(
      category
    );

    ExpenseCategoryDTO result = expenseCategoryService.createCategory(createDTO);

    assertNotNull(result);
    assertEquals(category.getId(), result.getId());
    assertEquals("Food", result.getName());
    assertEquals("food-icon", result.getIcon());
    verify(expenseCategoryRepository, times(1)).save(any(ExpenseCategory.class));
  }

  @Test
  public void testGetAllSubCategories() {
    ExpenseSubCategory sub1 = getTestSubCategory();
    ExpenseSubCategory sub2 = new ExpenseSubCategory();
    sub2.setId(2L);
    sub2.setName("Restaurants");
    sub2.setIcon("restaurant-icon");
    sub2.setCategory(getTestCategory());

    when(expenseSubCategoryRepository.findByCategoryId(1L)).thenReturn(
      List.of(sub1, sub2)
    );

    List<ExpenseSubCategoryDTO> result = expenseCategoryService.getAllSubCategories(
      1L
    );

    assertEquals(2, result.size());
    assertEquals(sub1.getId(), result.get(0).getId());
    assertEquals(sub1.getName(), result.get(0).getName());
    assertEquals(sub1.getCategory().getId(), result.get(0).getCategoryId());

    assertEquals(sub2.getId(), result.get(1).getId());
  }

  @Test
  public void testGetAllSubCategories_Empty() {
    when(expenseSubCategoryRepository.findByCategoryId(1L)).thenReturn(
      List.of()
    );

    List<ExpenseSubCategoryDTO> result = expenseCategoryService.getAllSubCategories(
      1L
    );

    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetSubCategory_Success() {
    ExpenseSubCategory subCategory = getTestSubCategory();
    when(expenseSubCategoryRepository.findById(1L)).thenReturn(
      Optional.of(subCategory)
    );

    ExpenseSubCategory result = expenseCategoryService.getSubCategory(1L);

    assertNotNull(result);
    assertEquals(subCategory.getId(), result.getId());
    assertEquals(subCategory.getName(), result.getName());
  }

  @Test
  public void testGetSubCategory_NotFound() {
    when(expenseSubCategoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      expenseCategoryService.getSubCategory(99L)
    );
  }

  @Test
  public void testCreateSubCategory_Success() {
    ExpenseCategory category = getTestCategory();
    ExpenseSubCategory subCategory = getTestSubCategory();
    ExpenseSubCategoryCreateDTO createDTO = new ExpenseSubCategoryCreateDTO(
      "Groceries",
      "grocery-icon",
      1L
    );

    when(expenseCategoryRepository.findById(1L)).thenReturn(
      Optional.of(category)
    );
    when(expenseSubCategoryRepository.save(any(ExpenseSubCategory.class))).thenReturn(
      subCategory
    );

    ExpenseSubCategoryDTO result = expenseCategoryService.createSubCategory(
      createDTO
    );

    assertNotNull(result);
    assertEquals(subCategory.getId(), result.getId());
    assertEquals("Groceries", result.getName());
    assertEquals(1L, result.getCategoryId());
    verify(expenseSubCategoryRepository, times(1)).save(
      any(ExpenseSubCategory.class)
    );
  }

  @Test
  public void testCreateSubCategory_CategoryNotFound() {
    ExpenseSubCategoryCreateDTO createDTO = new ExpenseSubCategoryCreateDTO(
      "Groceries",
      "grocery-icon",
      99L
    );

    when(expenseCategoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      expenseCategoryService.createSubCategory(createDTO)
    );
    verify(expenseSubCategoryRepository, never()).save(any());
  }
}
