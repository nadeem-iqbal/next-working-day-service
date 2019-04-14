package uk.gov.dwp.controller;

import uk.gov.dwp.exception.NotEnoughDataException;
import uk.gov.dwp.model.WorkingDay;
import uk.gov.dwp.service.WorkingDayService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureWebClient
@RunWith(SpringJUnit4ClassRunner.class)
public class WorkingDayControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private WorkingDayService workingDayService;

  @Before
  public void setUp() {
    WorkingDay workingDay = WorkingDay.builder()
            .date(LocalDate.now())
            .dayOfWeek(LocalDate.now().getDayOfWeek())
            .build();

    when(workingDayService.getNextWorkingDay(any())).thenReturn(workingDay);
  }

  @Test
  public void givenNoAfterDateWhenApiCalledThenTodayDateIsUsedToCallService() throws Exception {
    LocalDate expectedDateForServiceCall = LocalDate.now();

    mockMvc.perform(get("/next-working-day"))
            .andExpect(status().is2xxSuccessful());

    verify(workingDayService).getNextWorkingDay(expectedDateForServiceCall);
  }

  @Test
  public void givenValidAfterDateWhenApiCalledThenGivenDateIsUsedToCallService() throws Exception {
    LocalDate expectedDateForServiceCall = LocalDate.of(2019, 12, 12);

    mockMvc.perform(get("/next-working-day")
            .param("after", expectedDateForServiceCall.toString()))
            .andExpect(status().is2xxSuccessful());

    verify(workingDayService).getNextWorkingDay(expectedDateForServiceCall);
  }

  @Test
  public void givenInvalidAfterDateWhenApiCalledThenBadRequestResponseIsReturned() throws Exception {
    mockMvc.perform(get("/next-working-day")
            .param("after", "2019"))
            .andExpect(status().isBadRequest());

    verifyZeroInteractions(workingDayService);
  }

  @Test
  public void givenCallDownstreamToFailWhenApiCalledThenConflictResponseIsReturned() throws Exception {
    LocalDate expectedDateForServiceCall = LocalDate.now();

    when(workingDayService.getNextWorkingDay(any())).thenThrow(NotEnoughDataException.class);

    mockMvc.perform(get("/next-working-day"))
            .andExpect(status().isConflict());

    verify(workingDayService).getNextWorkingDay(expectedDateForServiceCall);
  }

}