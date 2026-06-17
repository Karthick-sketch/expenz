package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.expenses.dto.ExpenseDTO;
import com.karthick.expenz.expenses.dto.ExpenseUpdateDTO;
import com.karthick.expenz.expenses.entity.Expense;
import com.karthick.expenz.expenses.repository.ExpenseRepository;
import com.karthick.expenz.expenses.service.ExpenseService;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.service.UserService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
  private UserService userService;

  @InjectMocks
  private ExpenseService expenseService;

  private final Date date = new Date();

  private Expense getTestExpenseData() {
    Expense expense = new Expense();
    expense.setId(1);
    expense.setAmount(50_000.0);
    expense.setCategory("electronics");
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
    return dto;
  }

  private void assertExpenseEqualsDTO(Expense expense, ExpenseDTO dto) {
    assertEquals(expense.getId(), dto.getId());
    assertEquals(expense.getAmount(), dto.getAmount(), 0.001);
    assertEquals(expense.getTitle(), dto.getTitle());
    assertEquals(expense.getDescription(), dto.getDescription());
    assertEquals(expense.getCategory(), dto.getCategory());
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

    List<ExpenseDTO> validExpenses = expenseService.fetchThisMonthExpenses(
      mockExpense.getUser().getId()
    );
    assertEquals(1, validExpenses.size());
    assertExpenseEqualsDTO(mockExpense, validExpenses.get(0));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchExpensesByMonthAndYear() {
    LocalDate localDate = date
      .toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
    int month = localDate.getMonthValue(),
      year = localDate.getYear();

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
    LocalDate localDate = date
      .toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
    int month = localDate.getMonthValue(),
      year = localDate.getYear();

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

    ExpenseUpdateDTO updatedFields = new ExpenseUpdateDTO(
      45_000.0,
      mockExpense.getTitle(),
      mockExpense.getDescription(),
      mockExpense.getCategory(),
      mockExpense.isIncome()
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
}
