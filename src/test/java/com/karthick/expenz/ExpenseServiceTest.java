package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupCreateDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupDTO;
import com.karthick.expenz.expenses.dto.ExpenseGroupListDTO;
import com.karthick.expenz.expenses.dto.ExpenseListDTO;
import com.karthick.expenz.expenses.dto.ExpenseUpdateDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.entity.ExpenseGroup;
import com.karthick.expenz.expenses.entity.ExpenseSubCategory;
import com.karthick.expenz.expenses.repository.ExpenseGroupRepository;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.repository.ExpenseSubCategoryRepository;
import com.karthick.expenz.expenses.service.ExpenseService;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

  @Mock
  private ExpenseRepository expenseRepository;

  @Mock
  private ExpenseGroupRepository expenseGroupRepository;

  @Mock
  private ExpenseSubCategoryRepository expenseSubCategoryRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private ExpenseService expenseService;

  private final LocalDate date = LocalDate.now();

  private ExpenseGroup getTestExpenseGroupData() {
    ExpenseGroup group = new ExpenseGroup();
    group.setId(1L);
    group.setTitle("Gaming");
    group.setDescription("Gaming expenses");
    return group;
  }

  private Expense getTestExpenseData() {
    Expense expense = new Expense();
    expense.setId(1);
    expense.setAmount(50_000.0);
    ExpenseSubCategory category = new ExpenseSubCategory();
    category.setName("electronics");
    expense.setCategory(category);
    expense.setIncome(false);
    expense.setTitle("Playstation 5");
    expense.setDescription("Play next generation games");
    expense.setDateAdded(date);

    User user = new User();
    user.setId(1);
    user.setName("Michael De Santa");
    user.setEmail("michaeldesanta@eyefind.com");
    user.setPassword("gta5");
    expense.setUser(user);

    expense.setExpenseGroup(getTestExpenseGroupData());

    return expense;
  }

  private ExpenseDTO getTestExpenseDTOData() {
    ExpenseDTO dto = new ExpenseDTO();
    dto.setAmount(50_000.0);
    dto.setCategory("electronics");
    dto.setIncome(false);
    dto.setTitle("Playstation 5");
    dto.setDescription("Play next generation games");
    dto.setDateAdded(date);
    dto.setExpenseGroupId(1L);
    return dto;
  }

  private void assertExpenseEqualsDTO(Expense expense, ExpenseDTO dto) {
    assertEquals(expense.getId(), dto.getId());
    assertEquals(expense.getAmount(), dto.getAmount(), 0.001);
    assertEquals(expense.getTitle(), dto.getTitle());
    assertEquals(expense.getDescription(), dto.getDescription());
    assertEquals(expense.getCategory() != null ? expense.getCategory().getName() : null, dto.getCategory());
    assertEquals(expense.isIncome(), dto.isIncome());
    assertEquals(expense.getDateAdded(), dto.getDateAdded());
  }

  @Test
  public void testFindExpensesById() {
    Expense mockExpense = getTestExpenseData();
    when(
      expenseRepository.findByIdAndUserId(
        mockExpense.getId(),
        mockExpense.getUser().getId()
      )
    ).thenReturn(Optional.of(mockExpense));

    Expense validExpense = expenseService.findExpense(
      mockExpense.getId(),
      mockExpense.getUser().getId()
    );
    Executable wrongId = () ->
      expenseService.findExpense(2, mockExpense.getUser().getId());
    Executable wrongUserId = () ->
      expenseService.findExpense(mockExpense.getId(), 2);

    assertEquals(mockExpense, validExpense);
    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(EntityNotFoundException.class, wrongUserId);
  }

  @Test
  public void testFindExpenseDTOById() {
    Expense mockExpense = getTestExpenseData();
    when(
      expenseRepository.findByIdAndUserId(
        mockExpense.getId(),
        mockExpense.getUser().getId()
      )
    ).thenReturn(Optional.of(mockExpense));

    ExpenseDTO validExpense = expenseService.findExpenseDTO(
      mockExpense.getId(),
      mockExpense.getUser().getId()
    );
    Executable wrongId = () ->
      expenseService.findExpenseDTO(2, mockExpense.getUser().getId());
    Executable wrongUserId = () ->
      expenseService.findExpenseDTO(mockExpense.getId(), 2);

    assertExpenseEqualsDTO(mockExpense, validExpense);
    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(EntityNotFoundException.class, wrongUserId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchAllExpensesForCurrentMonth() {
    Expense mockExpense = getTestExpenseData();
    when(expenseRepository.findAll(any(Specification.class))).thenReturn(
      List.of(mockExpense)
    );

    ExpenseListDTO validExpenses = expenseService.fetchThisMonthExpenses(
      mockExpense.getUser().getId()
    );
    assertEquals(1, validExpenses.expenses().size());
    assertExpenseEqualsDTO(mockExpense, validExpenses.expenses().get(0));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchExpensesByMonthAndYear() {
    int month = date.getMonthValue(),
      year = date.getYear();

    Expense mockExpense = getTestExpenseData();
    when(expenseRepository.findAll(any(Specification.class))).thenReturn(
      List.of(mockExpense)
    );

    List<ExpenseDTO> validExpenses = expenseService.fetchExpenses(
      month,
      year,
      null,
      mockExpense.getUser().getId()
    );
    assertEquals(1, validExpenses.size());
    assertExpenseEqualsDTO(mockExpense, validExpenses.get(0));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchExpensesByTypeMonthAndYear() {
    int month = date.getMonthValue(),
      year = date.getYear();

    Expense mockExpense = getTestExpenseData();
    // mockExpense.isIncome() == false, so fetching expenses (false) returns results,
    // while fetching incomes (true) returns empty list
    when(expenseRepository.findAll(any(Specification.class)))
      .thenReturn(List.of(mockExpense))
      .thenReturn(List.of());

    List<ExpenseDTO> validExpenses = expenseService.fetchExpenses(
      month,
      year,
      false,
      mockExpense.getUser().getId()
    );
    List<ExpenseDTO> validIncomes = expenseService.fetchExpenses(
      month,
      year,
      true,
      mockExpense.getUser().getId()
    );
    assertEquals(1, validExpenses.size());
    assertExpenseEqualsDTO(mockExpense, validExpenses.get(0));
    assertTrue(validIncomes.isEmpty());
  }

  @Test
  public void testCreateNewExpense() {
    Expense mockExpense = getTestExpenseData();
    ExpenseDTO mockExpenseDTO = getTestExpenseDTOData();

    when(userService.findUser(mockExpense.getUser().getId())).thenReturn(
      mockExpense.getUser()
    );
    when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);
    when(expenseGroupRepository.findById(1L)).thenReturn(
      Optional.of(mockExpense.getExpenseGroup())
    );
    when(expenseSubCategoryRepository.findByName(mockExpenseDTO.getCategory())).thenReturn(
      Optional.of(mockExpense.getCategory())
    );

    ExpenseDTO expense = expenseService.createExpense(
      mockExpenseDTO,
      mockExpense.getUser().getId()
    );

    assertExpenseEqualsDTO(mockExpense, expense);
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }

  @Test
  public void testUpdateExpenseById() {
    Expense mockExpense = getTestExpenseData();
    when(
      expenseRepository.findByIdAndUserId(
        mockExpense.getId(),
        mockExpense.getUser().getId()
      )
    ).thenReturn(Optional.of(mockExpense));
    when(expenseRepository.save(mockExpense)).thenReturn(mockExpense);
    when(expenseSubCategoryRepository.findByName(any())).thenReturn(
      Optional.of(mockExpense.getCategory())
    );

    ExpenseUpdateDTO updatedFields = new ExpenseUpdateDTO(
      45_000.0,
      mockExpense.getTitle(),
      mockExpense.getDescription(),
      mockExpense.getCategory().getName(),
      mockExpense.isIncome(),
      mockExpense.getDateAdded(),
      mockExpense.getExpenseGroup().getId()
    );
    ExpenseDTO validExpense = expenseService.updateExpense(
      mockExpense.getId(),
      updatedFields,
      mockExpense.getUser().getId()
    );
    Executable wrongId = () ->
      expenseService.updateExpense(
        2,
        updatedFields,
        mockExpense.getUser().getId()
      );
    Executable wrongUserId = () ->
      expenseService.updateExpense(mockExpense.getId(), updatedFields, 2);

    assertEquals(45_000.0, validExpense.getAmount(), 0.001);
    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(EntityNotFoundException.class, wrongUserId);
    verify(expenseRepository, times(1)).save(mockExpense);
  }

  @Test
  public void testDeleteExpenseById() {
    Expense mockExpense = getTestExpenseData();
    when(
      expenseRepository.findByIdAndUserId(
        mockExpense.getId(),
        mockExpense.getUser().getId()
      )
    ).thenReturn(Optional.of(mockExpense));

    expenseService.deleteExpense(
      mockExpense.getId(),
      mockExpense.getUser().getId()
    );
    Executable wrongId = () ->
      expenseService.deleteExpense(2, mockExpense.getUser().getId());
    Executable wrongUserId = () ->
      expenseService.deleteExpense(mockExpense.getId(), 2);

    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(EntityNotFoundException.class, wrongUserId);
    verify(expenseRepository, times(1)).delete(mockExpense);
  }

  @Test
  public void testFetchDashboardData() {
    Expense mockExpense = getTestExpenseData();
    long userId = mockExpense.getUser().getId();

    when(expenseRepository.getTotalExpenses(userId, false)).thenReturn(
      50_000.0
    );
    when(expenseRepository.getTotalExpenses(userId, true)).thenReturn(
      100_000.0
    );
    when(expenseRepository.countByIncomeAndUserId(false, userId)).thenReturn(
      1L
    );
    when(expenseRepository.countByIncomeAndUserId(true, userId)).thenReturn(2L);
    when(expenseRepository.getRecentExpenses(userId)).thenReturn(
      List.of(mockExpense)
    );

    var dashboard = expenseService.fetchDashboardData(userId);

    assertEquals(50_000.0, dashboard.getTotalExpenses(), 0.001);
    assertEquals(100_000.0, dashboard.getTotalIncome(), 0.001);
    assertEquals(50_000.0, dashboard.getBalance(), 0.001);
    assertEquals(1L, dashboard.getTotalExpenseCount());
    assertEquals(2L, dashboard.getTotalIncomeCount());
    assertEquals(1, dashboard.getRecentExpenses().size());
    assertExpenseEqualsDTO(mockExpense, dashboard.getRecentExpenses().get(0));
  }

  @Test
  public void testCreateExpenseGroup() {
    Expense mockExpense = getTestExpenseData();
    ExpenseGroup mockGroup = getTestExpenseGroupData();
    mockGroup.setUser(mockExpense.getUser());

    when(userService.findUser(mockExpense.getUser().getId())).thenReturn(
      mockExpense.getUser()
    );
    when(expenseGroupRepository.save(any(ExpenseGroup.class))).thenReturn(
      mockGroup
    );
    // groups with no expenses yet
    mockGroup.setExpenses(List.of());

    ExpenseGroupCreateDTO createDTO = new ExpenseGroupCreateDTO(
      "Gaming",
      "Gaming expenses"
    );
    ExpenseGroupDTO result = expenseService.createExpenseGroup(
      createDTO,
      mockExpense.getUser().getId()
    );

    assertEquals(mockGroup.getId(), result.id());
    assertEquals(mockGroup.getTitle(), result.title());
    assertEquals(mockGroup.getDescription(), result.description());
    assertEquals(0, result.totalExpensesCount());
    assertEquals(0, result.totalIncomesCount());
    verify(expenseGroupRepository, times(1)).save(any(ExpenseGroup.class));
  }

  @Test
  public void testFetchExpenseGroups() {
    Expense mockExpense = getTestExpenseData();
    ExpenseGroup mockGroup = getTestExpenseGroupData();
    long userId = mockExpense.getUser().getId();

    when(expenseGroupRepository.findByUserId(userId)).thenReturn(
      List.of(mockGroup)
    );
    when(
      expenseRepository.countByIncomeAndUserId(false, mockGroup.getId())
    ).thenReturn(3L);
    when(
      expenseRepository.countByIncomeAndUserId(true, mockGroup.getId())
    ).thenReturn(1L);
    when(
      expenseRepository.getTotalExpensesInGroup(mockGroup.getId(), false)
    ).thenReturn(150_000.0);
    when(
      expenseRepository.getTotalExpensesInGroup(mockGroup.getId(), true)
    ).thenReturn(200_000.0);

    List<ExpenseGroupListDTO> groups = expenseService.fetchExpenseGroups(
      userId
    );

    assertEquals(1, groups.size());
    ExpenseGroupListDTO dto = groups.get(0);
    assertEquals(mockGroup.getId(), dto.id());
    assertEquals(mockGroup.getTitle(), dto.title());
    assertEquals(3L, dto.expenseCount());
    assertEquals(1L, dto.incomeCount());
    assertEquals(150_000.0, dto.totalExpensesAmount(), 0.001);
    assertEquals(200_000.0, dto.totalIncomesAmount(), 0.001);
    assertEquals(50_000.0, dto.balanceAmount(), 0.001);
  }

  @Test
  public void testFetchExpenseGroupDTO() {
    Expense mockExpense = getTestExpenseData();
    ExpenseGroup mockGroup = getTestExpenseGroupData();
    mockGroup.setExpenses(List.of(mockExpense));
    long userId = mockExpense.getUser().getId();

    when(
      expenseGroupRepository.findByIdAndUserId(mockGroup.getId(), userId)
    ).thenReturn(Optional.of(mockGroup));

    ExpenseGroupDTO result = expenseService.fetchExpenseGroupDTO(
      mockGroup.getId(),
      userId
    );

    assertEquals(mockGroup.getId(), result.id());
    assertEquals(mockGroup.getTitle(), result.title());
    // one expense (income=false)
    assertEquals(1L, result.totalExpensesCount());
    assertEquals(0L, result.totalIncomesCount());
    assertEquals(mockExpense.getAmount(), result.totalExpensesAmount(), 0.001);
    assertEquals(0.0, result.totalIncomesAmount(), 0.001);
  }

  @Test
  public void testFetchExpenseGroupDTO_notFound() {
    when(expenseGroupRepository.findByIdAndUserId(99L, 1L)).thenReturn(
      Optional.empty()
    );

    assertThrows(EntityNotFoundException.class, () ->
      expenseService.fetchExpenseGroupDTO(99L, 1L)
    );
  }
}
