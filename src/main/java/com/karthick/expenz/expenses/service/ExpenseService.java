package com.karthick.expenz.expenses.service;

import com.karthick.expenz.enums.ExpenseDuration;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.*;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.entity.ExpenseGroup;
import com.karthick.expenz.expenses.repository.ExpenseGroupRepository;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.repository.ExpenseSubCategoryRepository;
import com.karthick.expenz.expenses.specification.ExpenseSpecification;
import com.karthick.expenz.filter.ExpenseFilter;
import com.karthick.expenz.users.service.UserService;
import java.time.DayOfWeek;
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
  private ExpenseSubCategoryRepository expenseSubCategoryRepository;

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

  public ExpenseListDTO fetchExpenses(ExpenseFilter filter, long userId) {
    try {
      Specification<Expense> spec = buildSpecification(filter, userId);
      return toExpenseListDTO(expenseRepository.findAll(spec));
    } catch (Exception ex) {
      throw new BadRequestException(ex.getMessage());
    }
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
    expenseSubCategoryRepository
      .findById(updatedExpense.subCategoryId())
      .ifPresent(expense::setSubCategory);
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
    dashboardDTO.setRecentExpenses(getExpenseDTOs(recentExpenses));
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

  public List<ExpenseGroupListDTO> fetchExpenseGroups(long userId) {
    return expenseGroupRepository
      .findByUserId(userId)
      .stream()
      .map(this::toExpenseGroupListDTO)
      .toList();
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
      .and(ExpenseSpecification.withSubCategory(filter.getSubCategoryId()))
      .and(ExpenseSpecification.withSearchTerm(filter.getSearchTerm()));
  }

  private void calculateDateRange(ExpenseFilter filter) {
    if (filter.getDuration() == null) {
      filter.setDuration(ExpenseDuration.ALL_TIME);
    }

    LocalDate fromDate = filter.getFromDate();
    LocalDate toDate = filter.getToDate();
    switch (filter.getDuration()) {
      case ALL_TIME:
        fromDate = null;
        toDate = null;
        break;
      case THIS_WEEK:
        fromDate = LocalDate.now().with(DayOfWeek.MONDAY);
        toDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        break;
      case LAST_WEEK:
        fromDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        toDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        break;
      case THIS_MONTH:
        fromDate = LocalDate.now().withDayOfMonth(1);
        toDate = LocalDate.now().withDayOfMonth(
          LocalDate.now().lengthOfMonth()
        );
        break;
      case LAST_MONTH:
        fromDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        toDate = LocalDate.now().withDayOfMonth(
          LocalDate.now().lengthOfMonth()
        );
        break;
      case THIS_YEAR:
        fromDate = LocalDate.now().withDayOfYear(1);
        toDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        break;
      case LAST_YEAR:
        fromDate = LocalDate.now().minusYears(1).withDayOfYear(1);
        toDate = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        break;
      case DATE_RANGE:
        break;
    }
    filter.setFromDate(fromDate);
    filter.setToDate(toDate);
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
    expenseSubCategoryRepository
      .findById(expenseDTO.getSubCategoryId())
      .ifPresent(expense::setSubCategory);
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

  private ExpenseListDTO toExpenseListDTO(List<Expense> expenses) {
    List<ExpenseDTO> expenseDTOs = getExpenseDTOs(expenses);
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
    return new ExpenseListDTO(
      totalExpensesCount,
      totalIncomeCount,
      totalExpensesAmount,
      totalIncomeAmount,
      balanceAmount,
      expenseDTOs
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
    return new ExpenseGroupDTO(
      expenseGroup.getId(),
      expenseGroup.getTitle(),
      expenseGroup.getDescription(),
      totalExpensesCount,
      totalIncomeCount,
      totalExpensesAmount,
      totalIncomeAmount,
      balanceAmount,
      expenseDTOs
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
