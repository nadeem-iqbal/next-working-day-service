package uk.gov.dwp.client;

import uk.gov.dwp.exception.NotEnoughDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.model.BankHolidays;

@Component
public class BankHolidaysClient {
  private final RestTemplate restTemplate;
  private static final String BANK_HOLIDAYS_SERVICE_URL = "https://www.gov.uk/bank-holidays.json";

  @Autowired
  public BankHolidaysClient(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public BankHolidays getBankHolidays() {
    BankHolidays bankHolidays;
    try {

      bankHolidays = restTemplate.getForEntity(BANK_HOLIDAYS_SERVICE_URL, BankHolidays.class).getBody();

    } catch (Exception ex) {
      throw new NotEnoughDataException();
    }

    return bankHolidays;
  }

}
