package uk.gov.dwp.controller;

import uk.gov.dwp.exception.DateFormatException;
import uk.gov.dwp.exception.NotEnoughDataException;
import uk.gov.dwp.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class ExceptionController {

  @ExceptionHandler(DateFormatException.class)
  public final ResponseEntity<ErrorResponse> handleDateFormatException() {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                    .code("400")
                    .message("The after date is not a valid date. Please provide date in ISO_LOCAL_DATE(YYYY-MM-DD) format.")
                    .build()
            );
  }

  @ExceptionHandler(NotEnoughDataException.class)
  public final ResponseEntity<ErrorResponse> handleNotEnoughDataException() {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder()
                    .code("409")
                    .message("Do not have enough data to resolve next working day.")
                    .build()
            );
  }
}
