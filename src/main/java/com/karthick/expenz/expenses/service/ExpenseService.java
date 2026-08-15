package com.karthick.expenz.expenses.service;

import static com.karthick.expenz.expenses.util.ExpenseUtils.*;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.*;
import com.karthick.expenz.expenses.dto.category.ExpenseCategoryDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.entity.ExpenseGroup;
import com.karthick.expenz.expenses.repository.ExpenseGroupRepository;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.specification.ExpenseSpecification;
import com.karthick.expenz.filter.ExpenseFilter;
import com.karthick.expenz.filter.ExpenseGroupFilter;
import com.karthick.expenz.users.service.UserService;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExpenseService {

  private ExpenseRepository expenseRepository;
  private ExpenseGroupRepository expenseGroupRepository;

  private UserService userService;
  private ExpenseCategoryService expenseCategoryService;

  public ExpenseDTO createExpense(ExpenseDTO expenseDTO, long userId) {
    try {
      Expense expense = toExpense(expenseDTO);
      expense.setUser(userService.findUser(userId));
      return toExpenseDTO(expenseRepository.save(expense));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public Page<ExpenseDTO> fetchExpenses(ExpenseFilter filter, long userId) {
    try {
      Specification<Expense> spec = buildSpecification(filter, userId);
      Page<ExpenseDTO> page = expenseRepository
        .findAll(spec, PageRequest.of(filter.getPage(), filter.getSize()))
        .map(this::toExpenseDTO);
      return page;
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public ExpenseSummaryDTO fetchSummary(ExpenseFilter filter, long userId) {
    try {
      Specification<Expense> spec = buildSpecification(filter, userId);
      return toExpenseSummaryDTO(
        getExpenseDTOs(expenseRepository.findAll(spec))
      );
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public Expense findExpense(long id, long userId) {
    return expenseRepository
      .findByIdAndUserId(id, userId)
      .orElseThrow(() -> new EntityNotFoundException(id, Expense.class));
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
    expenseCategoryService.getSubCategory(updatedExpense.subCategoryId());
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
    List<ExpenseDTO> recentExpenses = getExpenseDTOs(
      expenseRepository.getRecentExpenses(userId)
    );
    List<ExpenseCategoryDTO> categories =
      expenseCategoryService.getAllCategories();

    DashboardDTO dashboardDTO = new DashboardDTO();
    dashboardDTO.setBalance(balance);
    dashboardDTO.setTotalExpenses(totalExpenses);
    dashboardDTO.setTotalIncome(totalIncome);
    dashboardDTO.setTotalExpenseCount(totalExpenseCount);
    dashboardDTO.setTotalIncomeCount(totalIncomeCount);
    dashboardDTO.setRecentExpenses(recentExpenses);
    dashboardDTO.setExpensePieDataItems(
      calculatePieData(recentExpenses, categories, false)
    );
    dashboardDTO.setIncomePieDataItems(
      calculatePieData(recentExpenses, categories, true)
    );
    return dashboardDTO;
  }

  public ExpenseGroupDTO createExpenseGroup(
    ExpenseGroupCreateDTO expenseGroupCreateDTO,
    long userId
  ) {
    ExpenseGroup expenseGroup = new ExpenseGroup();
    expenseGroup.setTitle(expenseGroupCreateDTO.title());
    expenseGroup.setDescription(expenseGroupCreateDTO.description());
    expenseGroup.setUser(userService.findUser(userId));
    try {
      return toExpenseGroupDTO(expenseGroupRepository.save(expenseGroup));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
  }

  public Page<ExpenseGroupListDTO> fetchExpenseGroups(
    ExpenseGroupFilter filter,
    long userId
  ) {
    return expenseGroupRepository
      .findByUserId(userId, PageRequest.of(filter.getPage(), filter.getSize()))
      .map(this::toExpenseGroupListDTO);
  }

  public ExpenseGroupDTO fetchExpenseGroupDTO(long id, long userId) {
    return toExpenseGroupDTO(
      expenseGroupRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException(id, ExpenseGroup.class))
    );
  }

  private Specification<Expense> buildSpecification(
    ExpenseFilter filter,
    long userId
  ) {
    calculateDateRange(filter);
    Specification<Expense> spec = (root, query, criteriaBuilder) ->
      criteriaBuilder.conjunction();
    return spec
      .and(ExpenseSpecification.withUserId(userId))
      .and(ExpenseSpecification.withExpenseType(filter.getType()))
      .and(ExpenseSpecification.withFromDate(filter.getFromDate()))
      .and(ExpenseSpecification.withToDate(filter.getToDate()))
      .and(ExpenseSpecification.withCategory(filter.getCategoryId()))
      .and(ExpenseSpecification.withSubCategory(filter.getSubCategoryId()))
      .and(ExpenseSpecification.withSearchTerm(filter.getSearchTerm()))
      .and(ExpenseSpecification.orderByDateAddedDesc());
  }

  private List<ExpenseDTO> getExpenseDTOs(List<Expense> expenses) {
    if (expenses == null || expenses.isEmpty()) {
      return Collections.emptyList();
    }
    return expenses.stream().map(this::toExpenseDTO).toList();
  }

  private Expense toExpense(ExpenseDTO expenseDTO) {
    Expense expense = new Expense();
    expense.setAmount(expenseDTO.getAmount());
    expense.setTitle(expenseDTO.getTitle());
    expense.setDescription(expenseDTO.getDescription());
    expenseCategoryService.getSubCategory(expenseDTO.getSubCategoryId());
    expense.setIncome(expenseDTO.isIncome());
    expense.setDateAdded(expenseDTO.getDateAdded());
    if (expenseDTO.getExpenseGroupId() != null) {
      expense.setExpenseGroup(
        expenseGroupRepository.findById(expenseDTO.getExpenseGroupId()).get()
      );
    }
    return expense;
  }

  private ExpenseDTO toExpenseDTO(Expense expense) {
    return new ExpenseDTO(
      expense.getId(),
      expense.getAmount(),
      expense.getTitle(),
      expense.getDescription(),
      expense.getCategoryId(),
      expense.getSubCategory().getId(),
      expense.isIncome(),
      expense.getDateAdded(),
      expense.getExpenseGroupId()
    );
  }

  private ExpenseSummaryDTO toExpenseSummaryDTO(List<ExpenseDTO> expenses) {
    long totalExpensesCount = 0;
    long totalIncomeCount = 0;
    double totalExpensesAmount = 0.0;
    double totalIncomeAmount = 0.0;
    for (ExpenseDTO expense : expenses) {
      if (expense.isIncome()) {
        totalIncomeCount++;
        totalIncomeAmount += expense.getAmount();
      } else {
        totalExpensesCount++;
        totalExpensesAmount += expense.getAmount();
      }
    }
    double balanceAmount = totalIncomeAmount - totalExpensesAmount;
    List<ExpenseCategoryDTO> categories =
      expenseCategoryService.getAllCategories();

    return new ExpenseSummaryDTO(
      totalExpensesCount,
      totalIncomeCount,
      totalExpensesAmount,
      totalIncomeAmount,
      balanceAmount,
      calculatePieData(expenses, categories, false),
      calculatePieData(expenses, categories, true)
    );
  }

  private ExpenseGroupDTO toExpenseGroupDTO(ExpenseGroup expenseGroup) {
    List<ExpenseDTO> expenseDTOs = getExpenseDTOs(expenseGroup.getExpenses());
    long totalExpensesCount = 0;
    long totalIncomeCount = 0;
    double totalExpensesAmount = 0.0;
    double totalIncomeAmount = 0.0;
    for (ExpenseDTO expenseDTO : expenseDTOs) {
      if (expenseDTO.isIncome()) {
        totalIncomeCount++;
        totalIncomeAmount += expenseDTO.getAmount();
      } else {
        totalExpensesCount++;
        totalExpensesAmount += expenseDTO.getAmount();
      }
    }
    double balanceAmount = totalIncomeAmount - totalExpensesAmount;
    List<ExpenseCategoryDTO> categories =
      expenseCategoryService.getAllCategories();

    return new ExpenseGroupDTO(
      expenseGroup.getId(),
      expenseGroup.getTitle(),
      expenseGroup.getDescription(),
      totalExpensesCount,
      totalIncomeCount,
      totalExpensesAmount,
      totalIncomeAmount,
      balanceAmount,
      expenseDTOs,
      calculatePieData(expenseDTOs, categories, false),
      calculatePieData(expenseDTOs, categories, true)
    );
  }

  private ExpenseGroupListDTO toExpenseGroupListDTO(ExpenseGroup expenseGroup) {
    Long expenseCount = expenseRepository.countTotalExpensesInGroup(
      false,
      expenseGroup.getId()
    );
    Long incomeCount = expenseRepository.countTotalExpensesInGroup(
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
    return new ExpenseGroupListDTO(
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
