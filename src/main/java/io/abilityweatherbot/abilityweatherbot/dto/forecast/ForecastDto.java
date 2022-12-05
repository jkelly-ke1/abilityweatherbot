package io.abilityweatherbot.abilityweatherbot.dto.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;


@Getter
public class ForecastDto {

    @JsonProperty("list")
    List<ForecastDetails> forecastDetailsList;

}
