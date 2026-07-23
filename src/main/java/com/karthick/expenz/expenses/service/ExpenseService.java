package com.karthick.expenz.expenses.service;

import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.DashboardDTO;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupCreateDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupListDTO;
import com.karthick.expenz.expenses.dto.ExpenseListDTO;
import com.karthick.expenz.expenses.dto.ExpenseUpdateDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.entity.ExpenseGroup;
import com.karthick.expenz.expenses.repository.ExpenseGroupRepository;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.repository.ExpenseSubCategoryRepository;
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

  public ExpenseListDTO fetchThisMonthExpenses(long userId) {
    LocalDate today = LocalDate.now();
    Specification<Expense> spec = buildSpecification(
      userId,
      today.getMonthValue(),
      today.getYear(),
      null
    );
    try {
      return toExpenseListDTO(expenseRepository.findAll(spec));
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
    return getExpenseDTOs(expenseRepository.findAll(spec));
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
      .findByName(updatedExpense.category())
      .ifPresent(expense::setCategory);
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
      .findByName(expenseDTO.getCategory())
      .ifPresent(expense::setCategory);
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
      expense.getCategory() != null ? expense.getCategory().getName() : null,
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
