package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.karthick.expenz.enums.ExpenseDuration;
import com.karthick.expenz.enums.ExpenseType;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.*;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.entity.ExpenseCategory;
import com.karthick.expenz.expenses.entity.ExpenseGroup;
import com.karthick.expenz.expenses.entity.ExpenseSubCategory;
import com.karthick.expenz.expenses.repository.ExpenseGroupRepository;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.repository.ExpenseSubCategoryRepository;
import com.karthick.expenz.expenses.service.ExpenseService;
import com.karthick.expenz.filter.ExpenseFilter;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
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
    expense.setId(1L);
    expense.setAmount(50_000.0);

    ExpenseCategory category = new ExpenseCategory();
    category.setId(1L);
    category.setName("Entertainment");

    ExpenseSubCategory subCategory = new ExpenseSubCategory();
    subCategory.setId(1L);
    subCategory.setName("electronics");
    subCategory.setCategory(category);
    expense.setSubCategory(subCategory);

    expense.setIncome(false);
    expense.setTitle("Playstation 5");
    expense.setDescription("Play next generation games");
    expense.setDateAdded(date);

    User user = new User();
    user.setId(1L);
    user.setName("Michael De Santa");
    user.setEmail("michaeldesanta@eyefind.com");
    user.setPassword("gta5");
    expense.setUser(user);

    expense.setExpenseGroup(getTestExpenseGroupData());

    return expense;
  }

  private ExpenseDTO getTestExpenseDTOData() {
    ExpenseDTO dto = new ExpenseDTO();
    dto.setId(1L);
    dto.setAmount(50_000.0);
    dto.setCategoryId(1L);
    dto.setSubCategoryId(1L);
    dto.setIncome(false);
    dto.setTitle("Playstation 5");
    dto.setDescription("Play next generation games");
    dto.setDateAdded(date);
    dto.setExpenseGroupId(1L);
    return dto;
  }

  private ExpenseFilter getTestExpenseFilterData() {
    ExpenseFilter filter = new ExpenseFilter();
    filter.setType(ExpenseType.EXPENSE);
    filter.setDuration(ExpenseDuration.ALL_TIME);
    filter.setSubCategoryId(1L);
    filter.setSearchTerm("test");
    return filter;
  }

  private void assertExpenseEqualsDTO(Expense expense, ExpenseDTO dto) {
    assertEquals(expense.getId(), dto.getId());
    assertEquals(expense.getAmount(), dto.getAmount(), 0.001);
    assertEquals(expense.getTitle(), dto.getTitle());
    assertEquals(expense.getDescription(), dto.getDescription());
    if (expense.getSubCategory() != null) {
      assertEquals(expense.getSubCategory().getId(), dto.getSubCategoryId());
      if (expense.getSubCategory().getCategory() != null) {
        assertEquals(expense.getCategoryId(), dto.getCategoryId());
      }
    }
    assertEquals(expense.isIncome(), dto.isIncome());
    assertEquals(expense.getDateAdded(), dto.getDateAdded());
    assertEquals(expense.getExpenseGroupId(), dto.getExpenseGroupId());
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
      expenseService.findExpense(2L, mockExpense.getUser().getId());
    Executable wrongUserId = () ->
      expenseService.findExpense(mockExpense.getId(), 2L);

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
      expenseService.findExpenseDTO(2L, mockExpense.getUser().getId());
    Executable wrongUserId = () ->
      expenseService.findExpenseDTO(mockExpense.getId(), 2L);

    assertExpenseEqualsDTO(mockExpense, validExpense);
    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(EntityNotFoundException.class, wrongUserId);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchExpenses() {
    Expense mockExpense = getTestExpenseData();

    Expense mockIncome = getTestExpenseData();
    mockIncome.setId(2L);
    mockIncome.setIncome(true);
    mockIncome.setAmount(100_000.0);

    ExpenseFilter filter = getTestExpenseFilterData();
    when(expenseRepository.findAll(any(Specification.class))).thenReturn(
      List.of(mockExpense, mockIncome)
    );

    ExpenseListDTO validExpenses = expenseService.fetchExpenses(
      filter,
      mockExpense.getUser().getId()
    );
    assertEquals(2, validExpenses.expenses().size());
    assertEquals(1L, validExpenses.totalExpensesCount());
    assertEquals(1L, validExpenses.totalIncomesCount());
    assertEquals(50_000.0, validExpenses.totalExpensesAmount(), 0.001);
    assertEquals(100_000.0, validExpenses.totalIncomesAmount(), 0.001);
    assertEquals(50_000.0, validExpenses.balanceAmount(), 0.001);
    assertExpenseEqualsDTO(mockExpense, validExpenses.expenses().get(0));
    assertExpenseEqualsDTO(mockIncome, validExpenses.expenses().get(1));
  }

  @ParameterizedTest
  @NullSource
  @EnumSource(ExpenseDuration.class)
  @SuppressWarnings("unchecked")
  public void testFetchExpensesDurations(ExpenseDuration duration) {
    Expense mockExpense = getTestExpenseData();
    ExpenseFilter filter = getTestExpenseFilterData();
    filter.setDuration(duration);
    if (duration == ExpenseDuration.DATE_RANGE) {
      filter.setFromDate(LocalDate.now().minusDays(5));
      filter.setToDate(LocalDate.now());
    }

    when(expenseRepository.findAll(any(Specification.class))).thenReturn(
      List.of(mockExpense)
    );

    ExpenseListDTO validExpenses = expenseService.fetchExpenses(
      filter,
      mockExpense.getUser().getId()
    );
    assertEquals(1, validExpenses.expenses().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchExpenses_ExceptionThrowsBadRequestException() {
    ExpenseFilter filter = getTestExpenseFilterData();
    when(expenseRepository.findAll(any(Specification.class))).thenThrow(
      new RuntimeException("Database error")
    );

    assertThrows(BadRequestException.class, () ->
      expenseService.fetchExpenses(filter, 1L)
    );
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
    when(
      expenseSubCategoryRepository.findById(mockExpenseDTO.getSubCategoryId())
    ).thenReturn(Optional.of(mockExpense.getSubCategory()));

    ExpenseDTO expense = expenseService.createExpense(
      mockExpenseDTO,
      mockExpense.getUser().getId()
    );

    assertExpenseEqualsDTO(mockExpense, expense);
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }

  @Test
  public void testCreateNewExpenseWithoutGroupId() {
    Expense mockExpense = getTestExpenseData();
    mockExpense.setExpenseGroup(null);
    ExpenseDTO mockExpenseDTO = getTestExpenseDTOData();
    mockExpenseDTO.setExpenseGroupId(null);

    when(userService.findUser(mockExpense.getUser().getId())).thenReturn(
      mockExpense.getUser()
    );
    when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);
    when(
      expenseSubCategoryRepository.findById(mockExpenseDTO.getSubCategoryId())
    ).thenReturn(Optional.of(mockExpense.getSubCategory()));

    ExpenseDTO expense = expenseService.createExpense(
      mockExpenseDTO,
      mockExpense.getUser().getId()
    );

    assertEquals(mockExpense.getId(), expense.getId());
    assertNull(expense.getExpenseGroupId());
    verify(expenseRepository, times(1)).save(any(Expense.class));
  }

  @Test
  public void testCreateNewExpense_ExceptionThrowsBadRequestException() {
    Expense mockExpense = getTestExpenseData();
    ExpenseDTO mockExpenseDTO = getTestExpenseDTOData();

    when(userService.findUser(mockExpense.getUser().getId())).thenReturn(
      mockExpense.getUser()
    );
    when(expenseGroupRepository.findById(1L)).thenReturn(
      Optional.of(mockExpense.getExpenseGroup())
    );
    when(
      expenseSubCategoryRepository.findById(mockExpenseDTO.getSubCategoryId())
    ).thenReturn(Optional.of(mockExpense.getSubCategory()));
    when(expenseRepository.save(any(Expense.class))).thenThrow(
      new RuntimeException("Database error")
    );

    assertThrows(BadRequestException.class, () ->
      expenseService.createExpense(
        mockExpenseDTO,
        mockExpense.getUser().getId()
      )
    );
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
    when(expenseSubCategoryRepository.findById(any())).thenReturn(
      Optional.of(mockExpense.getSubCategory())
    );

    ExpenseUpdateDTO updatedFields = new ExpenseUpdateDTO(
      45_000.0,
      mockExpense.getTitle(),
      mockExpense.getDescription(),
      mockExpense.getCategoryId(),
      mockExpense.getSubCategory().getId(),
      mockExpense.isIncome(),
      mockExpense.getDateAdded(),
      mockExpense.getExpenseGroupId()
    );
    ExpenseDTO validExpense = expenseService.updateExpense(
      mockExpense.getId(),
      updatedFields,
      mockExpense.getUser().getId()
    );
    Executable wrongId = () ->
      expenseService.updateExpense(
        2L,
        updatedFields,
        mockExpense.getUser().getId()
      );
    Executable wrongUserId = () ->
      expenseService.updateExpense(mockExpense.getId(), updatedFields, 2L);

    assertEquals(45_000.0, validExpense.getAmount(), 0.001);
    assertThrows(EntityNotFoundException.class, wrongId);
    assertThrows(EntityNotFoundException.class, wrongUserId);
    verify(expenseRepository, times(1)).save(mockExpense);
  }

  @Test
  public void testUpdateExpense_ExceptionThrowsBadRequestException() {
    Expense mockExpense = getTestExpenseData();
    when(
      expenseRepository.findByIdAndUserId(
        mockExpense.getId(),
        mockExpense.getUser().getId()
      )
    ).thenReturn(Optional.of(mockExpense));
    when(expenseRepository.save(mockExpense)).thenThrow(
      new RuntimeException("Save error")
    );

    ExpenseUpdateDTO updatedFields = new ExpenseUpdateDTO(
      45_000.0,
      mockExpense.getTitle(),
      mockExpense.getDescription(),
      mockExpense.getCategoryId(),
      mockExpense.getSubCategory().getId(),
      mockExpense.isIncome(),
      mockExpense.getDateAdded(),
      mockExpense.getExpenseGroupId()
    );

    assertThrows(BadRequestException.class, () ->
      expenseService.updateExpense(
        mockExpense.getId(),
        updatedFields,
        mockExpense.getUser().getId()
      )
    );
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
      expenseService.deleteExpense(2L, mockExpense.getUser().getId());
    Executable wrongUserId = () ->
      expenseService.deleteExpense(mockExpense.getId(), 2L);

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

    DashboardDTO dashboard = expenseService.fetchDashboardData(userId);

    assertEquals(50_000.0, dashboard.getTotalExpenses(), 0.001);
    assertEquals(100_000.0, dashboard.getTotalIncome(), 0.001);
    assertEquals(50_000.0, dashboard.getBalance(), 0.001);
    assertEquals(1L, dashboard.getTotalExpenseCount());
    assertEquals(2L, dashboard.getTotalIncomeCount());
    assertEquals(1, dashboard.getRecentExpenses().size());
    assertExpenseEqualsDTO(mockExpense, dashboard.getRecentExpenses().get(0));
  }

  @Test
  public void testFetchDashboardDataWithNullOrEmptyRecentExpenses() {
    long userId = 1L;

    when(expenseRepository.getTotalExpenses(userId, false)).thenReturn(0.0);
    when(expenseRepository.getTotalExpenses(userId, true)).thenReturn(0.0);
    when(expenseRepository.countByIncomeAndUserId(false, userId)).thenReturn(0L);
    when(expenseRepository.countByIncomeAndUserId(true, userId)).thenReturn(0L);
    when(expenseRepository.getRecentExpenses(userId)).thenReturn(null);

    DashboardDTO dashboard = expenseService.fetchDashboardData(userId);

    assertEquals(0.0, dashboard.getTotalExpenses(), 0.001);
    assertEquals(0.0, dashboard.getTotalIncome(), 0.001);
    assertEquals(0.0, dashboard.getBalance(), 0.001);
    assertTrue(dashboard.getRecentExpenses().isEmpty());
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
  public void testCreateExpenseGroup_ExceptionThrowsBadRequestException() {
    Expense mockExpense = getTestExpenseData();
    ExpenseGroupCreateDTO createDTO = new ExpenseGroupCreateDTO(
      "Gaming",
      "Gaming expenses"
    );
    when(userService.findUser(mockExpense.getUser().getId())).thenReturn(
      mockExpense.getUser()
    );
    when(expenseGroupRepository.save(any(ExpenseGroup.class))).thenThrow(
      new RuntimeException("Database error")
    );

    assertThrows(BadRequestException.class, () ->
      expenseService.createExpenseGroup(
        createDTO,
        mockExpense.getUser().getId()
      )
    );
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
      expenseRepository.countTotalExpensesInGroup(false, mockGroup.getId())
    ).thenReturn(3L);
    when(
      expenseRepository.countTotalExpensesInGroup(true, mockGroup.getId())
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
    Expense mockIncome = getTestExpenseData();
    mockIncome.setId(2L);
    mockIncome.setIncome(true);
    mockIncome.setAmount(100_000.0);

    ExpenseGroup mockGroup = getTestExpenseGroupData();
    mockGroup.setExpenses(List.of(mockExpense, mockIncome));
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
    assertEquals(1L, result.totalExpensesCount());
    assertEquals(1L, result.totalIncomesCount());
    assertEquals(mockExpense.getAmount(), result.totalExpensesAmount(), 0.001);
    assertEquals(100_000.0, result.totalIncomesAmount(), 0.001);
    assertEquals(50_000.0, result.balanceAmount(), 0.001);
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

