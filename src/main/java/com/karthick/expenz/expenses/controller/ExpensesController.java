package com.karthick.expenz.expenses.controller;

import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.service.ExpenseService;
import com.karthick.expenz.security.UserSession;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/expense")
public class ExpensesController {

  private ExpenseService expenseService;
  private UserSession userSession;

  @GetMapping("/all")
  public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
    return new ResponseEntity<>(
      expenseService.fetchAllExpenses(userSession.getAuthenticatedUserId()),
      HttpStatus.OK
    );
  }

  @GetMapping
  public ResponseEntity<List<ExpenseDTO>> getAllExpensesByMonthAndYear(
    @RequestParam int month,
    int year
  ) {
    return new ResponseEntity<>(
      expenseService.fetchExpensesByMonthAndYear(
        month,
        year,
        userSession.getAuthenticatedUserId()
      ),
      HttpStatus.OK
    );
  }

  @GetMapping("/expenses")
  public ResponseEntity<List<ExpenseDTO>> getExpensesByMonthAndYear(
    @RequestParam int month,
    int year
  ) {
    return new ResponseEntity<>(
      expenseService.fetchExpensesByTypeMonthAndYear(
        false,
        month,
        year,
        userSession.getAuthenticatedUserId()
      ),
      HttpStatus.OK
    );
  }

  @GetMapping("/incomes")
  public ResponseEntity<List<ExpenseDTO>> getIncomesByMonthAndYear(
    @RequestParam int month,
    int year
  ) {
    return new ResponseEntity<>(
      expenseService.fetchExpensesByTypeMonthAndYear(
        true,
        month,
        year,
        userSession.getAuthenticatedUserId()
      ),
      HttpStatus.OK
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDTO> getExpensesById(
    @PathVariable("id") long id
  ) {
    return new ResponseEntity<>(
      expenseService.findExpenseDTO(id, userSession.getAuthenticatedUserId()),
      HttpStatus.OK
    );
  }

  @PostMapping
  public ResponseEntity<ExpenseDTO> createNewExpense(
    @RequestBody Expense expense
  ) {
    return new ResponseEntity<>(
      expenseService.createExpense(
        expense,
        userSession.getAuthenticatedUserId()
      ),
      HttpStatus.CREATED
    );
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ExpenseDTO> updateExpenseById(
    @PathVariable("id") long id,
    @RequestBody Map<String, Object> newData
  ) {
    return new ResponseEntity<>(
      expenseService.updateExpense(
        id,
        newData,
        userSession.getAuthenticatedUserId()
      ),
      HttpStatus.OK
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteExpenseById(
    @PathVariable("id") long id
  ) {
    expenseService.deleteExpense(id, userSession.getAuthenticatedUserId());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
