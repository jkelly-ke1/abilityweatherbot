package io.abilityweatherbot.abilityweatherbot.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class Converter {

    // only for unix timestamp
    public String dateConverter(long weatherTimestamp, String pattern) {
        var formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
        var instant = Instant.ofEpochSecond(weatherTimestamp);
        return formatter.format(instant);
    }

    public String stringToEmojiConverter(String weatherCondition) {

        return switch (weatherCondition) {
            case "Thunderstorm" -> "⛈";
            case "Drizzle" -> "\uD83C\uDF26";
            case "Rain" -> "\uD83C\uDF27";
            case "Snow" -> "\uD83C\uDF28";
            case "Atmosphere" -> "\uD83C\uDF2B";
            case "Clear" -> "☀";
            case "Clouds" -> "\uD83C\uDF25";
            default -> "\uD83C\uDF24";
        };
    }

    public String rainProbabilityConverter(double pop) {
        int percentage = (int) (pop * 100);
        return (percentage <= 50) ? percentage + "%." : percentage + "%. Better take your umbrella!";
    }


}
