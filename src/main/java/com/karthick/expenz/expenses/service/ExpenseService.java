package com.karthick.expenz.expenses.service;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.DashboardDTO;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupDTO;
import com.karthick.expenz.expenses.dto.ExpenseUpdateDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.entity.ExpenseGroup;
import com.karthick.expenz.expenses.repository.ExpenseGroupRepository;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.specification.ExpenseSpecification;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExpenseService {

  private ExpenseRepository expenseRepository;
  private ExpenseGroupRepository expenseGroupRepository;

  private UserService userService;

  public ExpenseDTO createExpense(ExpenseDTO expenseDTO, long userId) {
    try {
      Expense expense = toExpense(expenseDTO);
      expense.setUser(userService.findUser(userId));
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      ex.printStackTrace();
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
    try {
      return getExpensesDTO(expenseRepository.findAll(spec));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public List<ExpenseDTO> fetchExpenses(
    Integer month,
    Integer year,
    Boolean type,
    long userId
  ) {
    Specification<Expense> spec = buildSpecification(userId, month, year, type);
    return getExpensesDTO(expenseRepository.findAll(spec));
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
    expense.setCurrencyCode(updatedExpense.currencyCode());
    expense.setTitle(updatedExpense.title());
    expense.setDescription(updatedExpense.description());
    expense.setCategory(updatedExpense.category());
    expense.setIncome(updatedExpense.income());
    expense.setDateAdded(updatedExpense.dateAdded());
    try {
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public void deleteExpense(long id, long userId) {
    expenseRepository.delete(findExpense(id, userId));
  }

  public DashboardDTO fetchDashboardData(Long userId) {
    Double totalExpenses = expenseRepository.getTotalExpenses(userId, false);
    Double totalIncome = expenseRepository.getTotalExpenses(userId, true);
    Double balance = totalIncome - totalExpenses;
    Long totalExpenseCount = expenseRepository.countByIncomeAndUserId(
      false,
      userId
    );
    Long totalIncomeCount = expenseRepository.countByIncomeAndUserId(
      true,
      userId
    );
    List<Expense> recentExpenses = expenseRepository.getRecentExpenses(userId);

    DashboardDTO dashboardDTO = new DashboardDTO();
    dashboardDTO.setBalance(balance);
    dashboardDTO.setTotalExpenses(totalExpenses);
    dashboardDTO.setTotalIncome(totalIncome);
    dashboardDTO.setTotalExpenseCount(totalExpenseCount);
    dashboardDTO.setTotalIncomeCount(totalIncomeCount);
    dashboardDTO.setRecentExpenses(getExpensesDTO(recentExpenses));
    return dashboardDTO;
  }

  public ExpenseGroupDTO createExpenseGroup(
    ExpenseGroupDTO expenseGroupDTO,
    long userId
  ) {
    ExpenseGroup expenseGroup = new ExpenseGroup();
    expenseGroup.setTitle(expenseGroupDTO.title());
    expenseGroup.setDescription(expenseGroupDTO.description());
    expenseGroup.setUser(userService.findUser(userId));
    try {
      return toExpenseGroupDTO(expenseGroupRepository.save(expenseGroup));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
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

  private List<ExpenseDTO> getExpensesDTO(List<Expense> expenses) {
    if (expenses == null || expenses.isEmpty()) {
      return Collections.emptyList();
    }
    return expenses.stream().map(this::toExpenseDTO).toList();
  }

  private Expense toExpense(ExpenseDTO expenseDTO) {
    Expense expense = new Expense();
    expense.setAmount(expenseDTO.getAmount());
    expense.setCurrencyCode(expenseDTO.getCurrencyCode());
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
      expense.getCurrencyCode(),
      expense.getTitle(),
      expense.getDescription(),
      expense.getCategory(),
      expense.isIncome(),
      expense.getDateAdded()
    );
  }

  private ExpenseGroupDTO toExpenseGroupDTO(ExpenseGroup expenseGroup) {
    Long expenseCount = expenseRepository.countByIncomeAndUserId(
      false,
      expenseGroup.getId()
    );
    Long incomeCount = expenseRepository.countByIncomeAndUserId(
      true,
      expenseGroup.getId()
    );
    Double totalExpensesAmount = expenseRepository.getTotalExpensesInGroup(
      expenseGroup.getId(),
      false
    );
    Double totalIncomesAmount = expenseRepository.getTotalExpensesInGroup(
      expenseGroup.getId(),
      true
    );
    Double balanceAmount = totalIncomesAmount - totalExpensesAmount;
    return new ExpenseGroupDTO(
      expenseGroup.getId(),
      expenseGroup.getTitle(),
      expenseGroup.getDescription(),
      expenseCount,
      incomeCount,
      totalExpensesAmount,
      totalIncomesAmount,
      balanceAmount
    );
  }
}
