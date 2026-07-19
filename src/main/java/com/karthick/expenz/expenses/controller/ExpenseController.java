package com.karthick.expenz.expenses.controller;

import com.karthick.expenz.auth.UserSession;
import com.karthick.expenz.expenses.dto.*;
import com.karthick.expenz.expenses.dto.category.*;
import com.karthick.expenz.expenses.service.*;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;
  private final ExpenseCategoryService expenseCategoryService;

  private final UserSession userSession;

  private Long userId() {
    return userSession.getAuthenticatedUserId();
  }

  @PostMapping
  public ResponseEntity<ExpenseDTO> createNewExpense(
    @RequestBody ExpenseDTO expenseDTO
  ) {
    return new ResponseEntity<>(
      expenseService.createExpense(expenseDTO, userId()),
      HttpStatus.CREATED
    );
  }

  @GetMapping
  public ResponseEntity<List<ExpenseDTO>> getExpenses(
    @RequestParam(required = false) Integer month,
    @RequestParam(required = false) Integer year,
    @RequestParam(required = false) Boolean type
  ) {
    return new ResponseEntity<>(
      expenseService.fetchExpenses(month, year, type, userId()),
      HttpStatus.OK
    );
  }

  @GetMapping("/this-month")
  public ResponseEntity<List<ExpenseDTO>> getThisMonthExpenses() {
    return new ResponseEntity<>(
      expenseService.fetchThisMonthExpenses(userId()),
      HttpStatus.OK
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDTO> getExpenseById(
    @PathVariable("id") long id
  ) {
    return new ResponseEntity<>(
      expenseService.findExpenseDTO(id, userId()),
      HttpStatus.OK
    );
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ExpenseDTO> updateExpenseById(
    @PathVariable("id") long id,
    @RequestBody ExpenseUpdateDTO updatedExpense
  ) {
    return new ResponseEntity<>(
      expenseService.updateExpense(id, updatedExpense, userId()),
      HttpStatus.OK
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteExpenseById(
    @PathVariable("id") long id
  ) {
    expenseService.deleteExpense(id, userId());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/dashboard")
  public ResponseEntity<DashboardDTO> getDashboardData() {
    return new ResponseEntity<>(
      expenseService.fetchDashboardData(userId()),
      HttpStatus.OK
    );
  }

  @PostMapping("/groups")
  public ResponseEntity<ExpenseGroupDTO> createNewExpenseGroup(
    @RequestBody ExpenseGroupCreateDTO expenseGroupCreateDTO
  ) {
    return new ResponseEntity<>(
      expenseService.createExpenseGroup(expenseGroupCreateDTO, userId()),
      HttpStatus.CREATED
    );
  }

  @GetMapping("/groups")
  public ResponseEntity<List<ExpenseGroupListDTO>> getExpenseGroups() {
    return new ResponseEntity<>(
      expenseService.fetchExpenseGroups(userId()),
      HttpStatus.OK
    );
  }

  @GetMapping("/groups/{id}")
  public ResponseEntity<ExpenseGroupDTO> getExpenseGroupById(
    @PathVariable("id") long id
  ) {
    return new ResponseEntity<>(
      expenseService.fetchExpenseGroupDTO(id, userId()),
      HttpStatus.OK
    );
  }

  @GetMapping("/categories")
  public ResponseEntity<List<ExpenseCategoryDTO>> getCategories() {
    return new ResponseEntity<>(
      expenseCategoryService.getAllCategories(),
      HttpStatus.OK
    );
  }

  @PostMapping("/categories")
  public ResponseEntity<ExpenseCategoryDTO> createNewCategory(
    @RequestBody ExpenseCategoryCreateDTO expenseCategoryCreateDTO
  ) {
    return new ResponseEntity<>(
      expenseCategoryService.createCategory(expenseCategoryCreateDTO),
      HttpStatus.CREATED
    );
  }

  @GetMapping("/categories/{id}/sub-categories")
  public ResponseEntity<List<ExpenseSubCategoryDTO>> getSubCategoryById(
    @PathVariable("id") long id
  ) {
    return new ResponseEntity<>(
      expenseCategoryService.getAllSubCategories(id),
      HttpStatus.OK
    );
  }
}
