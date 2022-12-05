package io.abilityweatherbot.abilityweatherbot.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;

@Getter
@ToString
public class CityDto {

    private String name;

    private HashMap<String, String> local_names;

    private double lat;

    private double lon;

    private String country;

}
