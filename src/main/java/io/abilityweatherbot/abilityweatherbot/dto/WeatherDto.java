package io.abilityweatherbot.abilityweatherbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class WeatherDto {

    @JsonProperty("weather")
    private List<Map<String, Object>> weatherInfo;

    @JsonProperty("main")
    private Map<String, Object> mainInfo;

    @JsonProperty("wind")
    private Map<String, Object> windInfo;

    @JsonProperty("dt")
    private int dt;

}
