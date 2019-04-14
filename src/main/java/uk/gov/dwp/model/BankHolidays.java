package uk.gov.dwp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankHolidays {
  @JsonProperty("england-and-wales")
  private EnglandAndWales englandAndWales;

  @JsonProperty("scotland")
  private Scotland scotland;

  @JsonProperty("northern-ireland")
  private NorthernIreland northernIreland;
}
