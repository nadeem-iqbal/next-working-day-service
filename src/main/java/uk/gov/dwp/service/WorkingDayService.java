package uk.gov.dwp.service;

import uk.gov.dwp.model.BankHolidays;
import uk.gov.dwp.model.Event;
import uk.gov.dwp.client.BankHolidaysClient;
import uk.gov.dwp.model.WorkingDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WorkingDayService {

  private final BankHolidaysClient bankHolidaysClient;

  @Value("${non.working.days}")
  private String[] nonWorkingDays;

  @Autowired
  public WorkingDayService(final BankHolidaysClient bankHolidaysClient) {
    this.bankHolidaysClient = bankHolidaysClient;
  }

  public WorkingDay getNextWorkingDay(final LocalDate afterDate) {
    final BankHolidays bankHolidays = bankHolidaysClient.getBankHolidays();
    final List<Event> bankHolidayEvents = Stream.concat(
            Stream.concat(
                    bankHolidays.getEnglandAndWales().getEvents().stream(),
                    bankHolidays.getNorthernIreland().getEvents().stream()).distinct(),
            bankHolidays.getScotland().getEvents().stream()
    ).distinct().collect(Collectors.toList());

    LocalDate nextWorkingDay = afterDate;
    do {

      nextWorkingDay = nextWorkingDay.plusDays(1);
    } while (isBankHoliday(nextWorkingDay, bankHolidayEvents)
            || isNonWorkingDay(nextWorkingDay.getDayOfWeek()));

    return WorkingDay.builder()
            .date(nextWorkingDay)
            .dayOfWeek(nextWorkingDay.getDayOfWeek())
            .build();
  }

  private boolean isBankHoliday(final LocalDate dateToCheck, final List<Event> bankHolidayEvents) {
    return bankHolidayEvents.stream()
            .anyMatch(holiday -> dateToCheck.equals(holiday.getDate()));
  }

  private boolean isNonWorkingDay(final DayOfWeek dayToCheck) {
    return Arrays.stream(nonWorkingDays)
            .anyMatch(day -> dayToCheck.name().equalsIgnoreCase(day));
  }
}
