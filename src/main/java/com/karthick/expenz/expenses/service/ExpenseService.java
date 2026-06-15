package com.karthick.expenz.expenses.service;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.users.service.UserService;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Service
@AllArgsConstructor
public class ExpenseService {

  private ExpenseRepository expenseRepository;
  private UserService userService;

  @Cacheable(value = "expense", key = "#id")
  public Expense findExpense(long id, long userId) {
    Optional<Expense> expense = expenseRepository.findById(id);
    if (expense.isEmpty() || expense.get().getUser().getId() != userId) {
      throw new EntityNotFoundException(id, Expense.class);
    }
    return expense.get();
  }

  public ExpenseDTO findExpenseDTO(long id, long userId) {
    return toExpenseDTO(findExpense(id, userId));
  }

  @Cacheable(value = "expenses:user", key = "#userId")
  public List<ExpenseDTO> fetchAllExpenses(long userId) {
    return expenseRepository
      .findByUserId(userId)
      .stream()
      .map(this::toExpenseDTO)
      .toList();
  }

  // not working, will fix it
  // @Cacheable(value = "expenses:user-month-year", key = "{#userId, #month, #year}")
  public List<ExpenseDTO> fetchExpensesByMonthAndYear(
    int month,
    int year,
    long userId
  ) {
    return expenseRepository
      .findExpensesByMonthAndYear(month, year, userId)
      .stream()
      .map(this::toExpenseDTO)
      .toList();
  }

  // not working, will fix it
  // @Cacheable(value = "expenses:user-type-month-year", key = "{#userId, #isItIncome, #month,
  // #year}")
  public List<ExpenseDTO> fetchExpensesByTypeMonthAndYear(
    boolean isItIncome,
    int month,
    int year,
    long userId
  ) {
    return expenseRepository
      .findExpensesByTypeMonthAndYear(isItIncome, month, year, userId)
      .stream()
      .map(this::toExpenseDTO)
      .toList();
  }

  @CacheEvict(value = "expenses:user", key = "#userId")
  public ExpenseDTO createExpense(Expense expense, long userId) {
    try {
      expense.setUser(userService.findUser(userId));
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @Caching(
    evict = {
      @CacheEvict(value = "expense", key = "#id"),
      @CacheEvict(value = "expenses:user", key = "#userId"),
    }
  )
  public ExpenseDTO updateExpense(
    long id,
    Map<String, Object> fields,
    long userId
  ) {
    Expense expense = findExpense(id, userId);
    try {
      fields.forEach((key, value) -> {
        Field field = ReflectionUtils.findField(Expense.class, key);
        if (field != null) {
          field.setAccessible(true);
          ReflectionUtils.setField(field, expense, value);
        }
      });
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  @Caching(
    evict = {
      @CacheEvict(value = "expense", key = "#id"),
      @CacheEvict(value = "expenses:user", key = "#userId"),
    }
  )
  public void deleteExpense(long id, long userId) {
    expenseRepository.delete(findExpense(id, userId));
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
