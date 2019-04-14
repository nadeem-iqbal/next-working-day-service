package uk.gov.dwp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.dwp.client.BankHolidaysClient;
import uk.gov.dwp.model.BankHolidays;
import uk.gov.dwp.model.EnglandAndWales;
import uk.gov.dwp.model.Event;
import uk.gov.dwp.model.NorthernIreland;
import uk.gov.dwp.model.Scotland;
import uk.gov.dwp.model.WorkingDay;

import java.time.LocalDate;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-friday-non-working-day-test.properties")
public class WorkingDayServiceFridayNonWorkingDayTest {

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
  public void givenThursdayWhenServiceCalledThenMondayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 11);
    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 15), nextWorkingDay.getDate());
    assertEquals(MONDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenThursdayBeforeFridayBankHolidayWhenServiceCalledThenMondayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 11);

    Event fridayBankHolidayEvent = Event.builder().date(givenDate.plusDays(1)).build();
    when(scotland.getEvents()).thenReturn(singletonList(fridayBankHolidayEvent));

    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 15), nextWorkingDay.getDate());
    assertEquals(MONDAY, nextWorkingDay.getDayOfWeek());
  }

  @Test
  public void givenThursdayBeforeMondayBankHolidayWhenServiceCalledThenTuesdayIsReturned() {
    LocalDate givenDate = LocalDate.of(2019, 4, 11);

    Event mondayBankHolidayEvent = Event.builder().date(givenDate.plusDays(4)).build();
    when(englandAndWales.getEvents()).thenReturn(singletonList(mondayBankHolidayEvent));

    WorkingDay nextWorkingDay = workingDayService.getNextWorkingDay(givenDate);

    assertEquals(LocalDate.of(2019, 4, 16), nextWorkingDay.getDate());
    assertEquals(TUESDAY, nextWorkingDay.getDayOfWeek());
  }

}