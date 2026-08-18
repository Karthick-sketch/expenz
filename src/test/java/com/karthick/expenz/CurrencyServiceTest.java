package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.karthick.expenz.currency.dto.CurrencyConversionRateDTO;
import com.karthick.expenz.currency.entity.CurrencyConversionRate;
import com.karthick.expenz.currency.repository.CurrencyConversionRateRepository;
import com.karthick.expenz.currency.service.CurrencyService;
import com.karthick.expenz.exception.BadRequestException;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

  @Mock
  private CurrencyConversionRateRepository currencyConversionRateRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private CurrencyService currencyService;

  private static final long USER_ID = 1L;
  private static final String FROM = "USD";
  private static final String TO = "INR";
  private static final BigDecimal RATE = BigDecimal.valueOf(83.5);

  private User getTestUser() {
    User user = new User();
    user.setId(USER_ID);
    user.setName("Kang");
    user.setEmail("kang@marvel.com");
    user.setPassword("encoded");
    return user;
  }

  private CurrencyConversionRateDTO getTestDTO() {
    return new CurrencyConversionRateDTO(FROM, TO, RATE);
  }

  private CurrencyConversionRate getTestEntity() {
    return new CurrencyConversionRate(FROM, TO, RATE, LocalDate.now(), getTestUser());
  }

  // ──────────────────────────────────────────────
  //  createCurrencyConversionRate()
  // ──────────────────────────────────────────────

  @Test
  public void testCreateCurrencyConversionRate_Success() {
    CurrencyConversionRate entity = getTestEntity();
    when(userService.findUser(USER_ID)).thenReturn(getTestUser());
    when(currencyConversionRateRepository.save(any(CurrencyConversionRate.class)))
      .thenReturn(entity);

    CurrencyConversionRateDTO result =
      currencyService.createCurrencyConversionRate(getTestDTO(), USER_ID);

    assertNotNull(result);
    assertEquals(FROM, result.fromCurrency());
    assertEquals(TO, result.toCurrency());
    assertEquals(RATE, result.rate());
    verify(currencyConversionRateRepository, times(1)).save(any(CurrencyConversionRate.class));
  }

  @Test
  public void testCreateCurrencyConversionRate_ExceptionThrowsBadRequestException() {
    when(userService.findUser(USER_ID)).thenReturn(getTestUser());
    when(currencyConversionRateRepository.save(any(CurrencyConversionRate.class)))
      .thenThrow(new RuntimeException("DB error"));

    assertThrows(BadRequestException.class, () ->
      currencyService.createCurrencyConversionRate(getTestDTO(), USER_ID)
    );
  }

  // ──────────────────────────────────────────────
  //  getCurrencyConversionRates()
  // ──────────────────────────────────────────────

  @Test
  public void testGetCurrencyConversionRates_ReturnsList() {
    CurrencyConversionRate entity1 = getTestEntity();
    CurrencyConversionRate entity2 =
      new CurrencyConversionRate(FROM, "EUR", BigDecimal.valueOf(0.92), LocalDate.now(), getTestUser());

    when(currencyConversionRateRepository.findByUserId(USER_ID))
      .thenReturn(List.of(entity1, entity2));

    List<CurrencyConversionRateDTO> result =
      currencyService.getCurrencyConversionRates(USER_ID);

    assertEquals(2, result.size());
    assertEquals(FROM, result.get(0).fromCurrency());
    assertEquals(TO, result.get(0).toCurrency());
    assertEquals(FROM, result.get(1).fromCurrency());
    assertEquals("EUR", result.get(1).toCurrency());
  }

  @Test
  public void testGetCurrencyConversionRates_ReturnsEmptyList() {
    when(currencyConversionRateRepository.findByUserId(USER_ID))
      .thenReturn(List.of());

    List<CurrencyConversionRateDTO> result =
      currencyService.getCurrencyConversionRates(USER_ID);

    assertTrue(result.isEmpty());
  }

  // ──────────────────────────────────────────────
  //  getCurrencyConversionRate()
  // ──────────────────────────────────────────────

  @Test
  public void testGetCurrencyConversionRate_Success() {
    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.of(getTestEntity()));

    CurrencyConversionRateDTO result =
      currencyService.getCurrencyConversionRate(FROM, TO, USER_ID);

    assertNotNull(result);
    assertEquals(FROM, result.fromCurrency());
    assertEquals(TO, result.toCurrency());
    assertEquals(RATE, result.rate());
  }

  @Test
  public void testGetCurrencyConversionRate_NotFound_ThrowsBadRequestException() {
    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.empty());

    assertThrows(BadRequestException.class, () ->
      currencyService.getCurrencyConversionRate(FROM, TO, USER_ID)
    );
  }

  // ──────────────────────────────────────────────
  //  upsertCurrencyConversionRate()
  // ──────────────────────────────────────────────

  @Test
  public void testUpsertCurrencyConversionRate_CreatesNew_WhenNotExists() {
    CurrencyConversionRateDTO dto = getTestDTO();
    CurrencyConversionRate entity = getTestEntity();

    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.empty());
    when(userService.findUser(USER_ID)).thenReturn(getTestUser());
    when(currencyConversionRateRepository.save(any(CurrencyConversionRate.class)))
      .thenReturn(entity);

    CurrencyConversionRateDTO result =
      currencyService.upsertCurrencyConversionRate(dto, USER_ID);

    assertNotNull(result);
    assertEquals(FROM, result.fromCurrency());
    assertEquals(TO, result.toCurrency());
    verify(currencyConversionRateRepository, times(1)).save(any(CurrencyConversionRate.class));
  }

  @Test
  public void testUpsertCurrencyConversionRate_UpdatesExisting() {
    BigDecimal newRate = BigDecimal.valueOf(84.0);
    CurrencyConversionRateDTO dto = new CurrencyConversionRateDTO(FROM, TO, newRate);
    CurrencyConversionRate existingEntity = getTestEntity();
    // Simulate updated entity returned from save
    CurrencyConversionRate updatedEntity =
      new CurrencyConversionRate(FROM, TO, newRate, LocalDate.now(), getTestUser());

    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.of(existingEntity));
    when(currencyConversionRateRepository.save(existingEntity)).thenReturn(updatedEntity);

    CurrencyConversionRateDTO result =
      currencyService.upsertCurrencyConversionRate(dto, USER_ID);

    assertNotNull(result);
    assertEquals(newRate, result.rate());
    verify(currencyConversionRateRepository, times(1)).save(existingEntity);
  }

  @Test
  public void testUpsertCurrencyConversionRate_UpdateFails_ThrowsBadRequestException() {
    CurrencyConversionRate existingEntity = getTestEntity();

    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.of(existingEntity));
    when(currencyConversionRateRepository.save(existingEntity))
      .thenThrow(new RuntimeException("DB error"));

    assertThrows(BadRequestException.class, () ->
      currencyService.upsertCurrencyConversionRate(getTestDTO(), USER_ID)
    );
  }

  // ──────────────────────────────────────────────
  //  findCurrencyConversionRate()
  // ──────────────────────────────────────────────

  @Test
  public void testFindCurrencyConversionRate_ReturnsPresent() {
    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.of(getTestEntity()));

    Optional<CurrencyConversionRate> result =
      currencyService.findCurrencyConversionRate(FROM, TO, USER_ID);

    assertTrue(result.isPresent());
    assertEquals(FROM, result.get().getFromCurrency());
    assertEquals(TO, result.get().getToCurrency());
  }

  @Test
  public void testFindCurrencyConversionRate_ReturnsEmpty() {
    when(currencyConversionRateRepository.findByFromCurrencyAndToCurrencyAndUserId(FROM, TO, USER_ID))
      .thenReturn(Optional.empty());

    Optional<CurrencyConversionRate> result =
      currencyService.findCurrencyConversionRate(FROM, TO, USER_ID);

    assertFalse(result.isPresent());
  }
}
