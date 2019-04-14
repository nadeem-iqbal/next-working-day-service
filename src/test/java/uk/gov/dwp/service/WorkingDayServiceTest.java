package uk.gov.dwp.service;

import uk.gov.dwp.model.BankHolidays;
import uk.gov.dwp.model.EnglandAndWales;
import uk.gov.dwp.model.Event;
import uk.gov.dwp.client.BankHolidaysClient;
import uk.gov.dwp.model.NorthernIreland;
import uk.gov.dwp.model.Scotland;
import uk.gov.dwp.model.WorkingDay;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Arrays;

import static java.time.DayOfWeek.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class WorkingDayServiceTest {

  @Autowired
  private WorkingDayService workingDayService;

  @MockBean
  private BankHolidaysClient bankHolidaysClient;

  @MockBean
  private EnglandAndWales englandAndWales;

  @MockBean
  private NorthernIreland northernIreland;

  @MockBean
  private Scotland scotland;

  @MockBean
  private BankHolidays bankHolidays;

  @Before
  public void setup() {
    when(bankHolidaysClient.getBankHolidays()).thenReturn(bankHolidays);
    when(bankHolidays.getEnglandAndWales()).thenReturn(englandAndWales);
    when(bankHolidays.getNorthernIreland()).thenReturn(northernIreland);
    when(bankHolidays.getScotland()).thenReturn(scotland);
  }

  @Test
  public void givenFridayWhenServiceCalledThenMondayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 12);
    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 15), nextWorkingDay.getDate());
    assertEquals(MONDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenTuesdayWhenServiceCalledThenWednesdayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 16);
    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 17), nextWorkingDay.getDate());
    assertEquals(WEDNESDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenThursdayBeforeFridayBankHolidayWhenServiceCalledThenMondayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 4);

    Event fridayBankHolidayEvent = Event.builder().date(givenDate.plusDays(1)).build();
    when(englandAndWales.getEvents()).thenReturn(singletonList(fridayBankHolidayEvent));

    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 8), nextWorkingDay.getDate());
    assertEquals(MONDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenThursdayBeforeFridayAndMondayBankHolidayWhenServiceCalledThenTuesdayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 4);

    Event fridayBankHolidayEvent = Event.builder().date(givenDate.plusDays(1)).build();
    Event mondayBankHolidayEvent = Event.builder().date(givenDate.plusDays(4)).build();
    when(englandAndWales.getEvents()).thenReturn(Arrays.asList(fridayBankHolidayEvent, mondayBankHolidayEvent));

    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 9), nextWorkingDay.getDate());
    assertEquals(TUESDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenFridayBeforeMondayBankHolidayWhenServiceCalledThenTuesdayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 5);

    Event mondayBankHolidayEvent = Event.builder().date(givenDate.plusDays(3)).build();
    when(englandAndWales.getEvents()).thenReturn(singletonList(mondayBankHolidayEvent));

    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 9), nextWorkingDay.getDate());
    assertEquals(TUESDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenMondayBeforeEnglandAndScotlandBankHolidayWhenServiceCalledThenThursdayIsReturned() {
    LocalDate givenDate = LocalDate.of(2018, 12, 31);

    Event englandNewYearBankHolidayEvent = Event.builder().date(givenDate.plusDays(1)).build();
    when(englandAndWales.getEvents()).thenReturn(singletonList(englandNewYearBankHolidayEvent));

    Event scotlandSecondJanuaryBankHolidayEvent = Event.builder().date(givenDate.plusDays(2)).build();
    when(scotland.getEvents()).thenReturn(singletonList(scotlandSecondJanuaryBankHolidayEvent));

    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 1, 3), nextWorkingDay.getDate());
    assertEquals(THURSDAY, nextWorkingDay.getDayOfWeek());

  }

}