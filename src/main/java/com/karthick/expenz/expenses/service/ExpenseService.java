package com.karthick.expenz.expenses.service;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.ExpenseUpdateDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.specification.ExpenseSpecification;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExpenseService {

  private ExpenseRepository expenseRepository;

  private UserService userService;

  public ExpenseDTO createExpense(ExpenseDTO expenseDTO, long userId) {
    try {
      Expense expense = toExpense(expenseDTO);
      expense.setUser(userService.findUser(userId));
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public List<ExpenseDTO> fetchThisMonthExpenses(long userId) {
    LocalDate today = LocalDate.now();
    Specification<Expense> spec = buildSpecification(
      userId,
      today.getMonthValue(),
      today.getYear(),
      null
    );
    return expenseRepository
      .findAll(spec)
      .stream()
      .map(this::toExpenseDTO)
      .toList();
  }

  public List<ExpenseDTO> fetchExpenses(
    Integer month,
    Integer year,
    Boolean type,
    long userId
  ) {
    Specification<Expense> spec = buildSpecification(userId, month, year, type);
    return expenseRepository
      .findAll(spec)
      .stream()
      .map(this::toExpenseDTO)
      .toList();
  }

  public Expense findExpense(long id, long userId) {
    Optional<Expense> expense = expenseRepository.findByIdAndUserId(id, userId);
    if (expense.isEmpty()) {
      throw new EntityNotFoundException(id, Expense.class);
    }
    return expense.get();
  }

  public ExpenseDTO findExpenseDTO(long id, long userId) {
    return toExpenseDTO(findExpense(id, userId));
  }

  public ExpenseDTO updateExpense(
    long id,
    ExpenseUpdateDTO updatedExpense,
    long userId
  ) {
    Expense expense = findExpense(id, userId);
    expense.setAmount(updatedExpense.amount());
    expense.setTitle(updatedExpense.title());
    expense.setDescription(updatedExpense.description());
    expense.setCategory(updatedExpense.category());
    expense.setIncome(updatedExpense.isIncome());
    try {
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public void deleteExpense(long id, long userId) {
    expenseRepository.delete(findExpense(id, userId));
  }

  private Specification<Expense> buildSpecification(
    long userId,
    Integer month,
    Integer year,
    Boolean type
  ) {
    Specification<Expense> spec = (root, query, criteriaBuilder) ->
      criteriaBuilder.conjunction();
    return spec
      .and(ExpenseSpecification.withUserId(userId))
      .and(ExpenseSpecification.withMonth(month))
      .and(ExpenseSpecification.withYear(year))
      .and(ExpenseSpecification.withExpenseType(type));
  }

  private Expense toExpense(ExpenseDTO expenseDTO) {
    Expense expense = new Expense();
    expense.setAmount(expenseDTO.getAmount());
    expense.setTitle(expenseDTO.getTitle());
    expense.setDescription(expenseDTO.getDescription());
    expense.setCategory(expenseDTO.getCategory());
    expense.setIncome(expenseDTO.isIncome());
    expense.setDateAdded(expenseDTO.getDateAdded());
    return expense;
  }

  private ExpenseDTO toExpenseDTO(Expense expense) {
    return new ExpenseDTO(
      expense.getId(),
      expense.getAmount(),
      expense.getTitle(),
      expense.getDescription(),
      expense.getCategory(),
      expense.isIncome(),
      expense.getDateAdded()
    );
  }
}
