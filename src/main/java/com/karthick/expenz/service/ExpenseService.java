package com.karthick.expenz.service;

import com.karthick.expenz.entity.Expense;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.repository.ExpenseRepository;
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

  @Cacheable(value = "expenses:user", key = "#userId")
  public List<Expense> fetchAllExpenses(long userId) {
    return expenseRepository.findByUserId(userId);
  }

  // not working, will fix it
  // @Cacheable(value = "expenses:user-month-year", key = "{#userId, #month, #year}")
  public List<Expense> fetchExpensesByMonthAndYear(
    int month,
    int year,
    long userId
  ) {
    return expenseRepository.findExpensesByMonthAndYear(month, year, userId);
  }

  // not working, will fix it
  // @Cacheable(value = "expenses:user-type-month-year", key = "{#userId, #isItIncome, #month,
  // #year}")
  public List<Expense> fetchExpensesByTypeMonthAndYear(
    boolean isItIncome,
    int month,
    int year,
    long userId
  ) {
    return expenseRepository.findExpensesByTypeMonthAndYear(
      isItIncome,
      month,
      year,
      userId
    );
  }

  @CacheEvict(value = "expenses:user", key = "#userId")
  public Expense createExpense(Expense expense, long userId) {
    try {
      expense.setUser(userService.findUser(userId));
      return expenseRepository.save(expense);
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
  public Expense updateExpense(
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
      return expenseRepository.save(expense);
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
}
