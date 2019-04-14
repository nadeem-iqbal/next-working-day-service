package uk.gov.dwp.controller;

import uk.gov.dwp.model.WorkingDay;
import uk.gov.dwp.exception.DateFormatException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.service.WorkingDayService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
public class WorkingDayController {

  private final WorkingDayService workingDayService;

  @Autowired
  public WorkingDayController(final WorkingDayService workingDayService) {
    this.workingDayService = workingDayService;
  }

  @GetMapping(value = {"/next-working-day"})
  public ResponseEntity<WorkingDay> getWorkingDay(@RequestParam(value = "after", required = false) final String after) {
    LocalDate afterDate = LocalDate.now();

    if (StringUtils.isNoneEmpty(after)) {

      try {
        afterDate = LocalDate.parse(after);
      } catch (DateTimeParseException dtpe) {
        throw new DateFormatException();
      }
    }

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(workingDayService.getNextWorkingDay(afterDate));
  }
}
