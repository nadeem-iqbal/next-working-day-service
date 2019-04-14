package uk.gov.dwp.client;

import uk.gov.dwp.exception.NotEnoughDataException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import uk.gov.dwp.model.BankHolidays;
import uk.gov.dwp.model.EnglandAndWales;
import uk.gov.dwp.model.Event;

import java.time.LocalDate;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringJUnit4ClassRunner.class)
@RestClientTest(BankHolidaysClient.class)
public class BankHolidaysClientTest {
  private String bankHolidaysResponse;
  private static final String DIVISON = "england-and-wales";
  private static final String TITLE = "Some title";
  private static final LocalDate DATE = LocalDate.of(2019, 4, 12);
  private static final String NOTES = "Some notes";
  private static final Boolean BUNTING = Boolean.TRUE;
  private static final String BANK_HOLIDAYS_SERVICE_URL = "https://www.gov.uk/bank-holidays.json";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockRestServiceServer mockServer;

  @Autowired
  private BankHolidaysClient bankHolidaysClient;

  @Before
  public void setUp() throws JsonProcessingException {
    final Event expectedHolidayEvent = Event.builder()
            .title(TITLE)
            .date(DATE)
            .notes(NOTES)
            .bunting(BUNTING)
            .build();

    final EnglandAndWales expectedEnglandAndWales = EnglandAndWales.builder()
            .division(DIVISON)
            .events(singletonList(expectedHolidayEvent))
            .build();

    final BankHolidays expectedBankHolidays = BankHolidays.builder()
            .englandAndWales(expectedEnglandAndWales)
            .build();

    bankHolidaysResponse = objectMapper.writeValueAsString(expectedBankHolidays);
  }

  @Test
  public void givenValidRequestWhenBankHolidaysApiCalledThenBankHolidaysReturned() {

    mockServer.expect(requestTo(BANK_HOLIDAYS_SERVICE_URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(bankHolidaysResponse, MediaType.APPLICATION_JSON));

    BankHolidays returnedBankHolidays = bankHolidaysClient.getBankHolidays();

    mockServer.verify();
    assertNull(returnedBankHolidays.getScotland());
    assertNull(returnedBankHolidays.getNorthernIreland());
    assertEquals(returnedBankHolidays.getEnglandAndWales().getEvents().size(), 1);
    assertEquals(DIVISON, returnedBankHolidays.getEnglandAndWales().getDivision());

    returnedBankHolidays.getEnglandAndWales().getEvents().forEach(returnedHolidayEvent -> {
      assertEquals(DATE, returnedHolidayEvent.getDate());
      assertEquals(TITLE, returnedHolidayEvent.getTitle());
      assertEquals(NOTES, returnedHolidayEvent.getNotes());
      assertEquals(BUNTING, returnedHolidayEvent.isBunting());
    });
  }

  @Test(expected = NotEnoughDataException.class)
  public void givenInvalidRequestWhenBankHolidaysApiCalledThenNotEnoughDataExceptionIsThrown() {

    mockServer.expect(requestTo(BANK_HOLIDAYS_SERVICE_URL))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withServerError());

    bankHolidaysClient.getBankHolidays();

    mockServer.verify();
  }

}