package io.abilityweatherbot.abilityweatherbot.dto.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ForecastDetails {

    @JsonProperty("dt")
    private int dt;

    @JsonProperty("main")
    Map<String, Object> main;

    @JsonProperty("weather")
    List<Map<String, Object>> weather;

    @JsonProperty("wind")
    Map<String, Object> wind;

    @JsonProperty("pop")
    double rainfallProbability;

}
