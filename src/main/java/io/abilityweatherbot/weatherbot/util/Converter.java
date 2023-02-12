package io.abilityweatherbot.weatherbot.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class Converter {

    // only for unix timestamp
    public String timestampConverter(long weatherTimestamp, String pattern) {
        var formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
        var instant = Instant.ofEpochSecond(weatherTimestamp);
        return formatter.format(instant);
    }

    public String localTimeChecker(long timestamp) {
        var convertedApiHour = timestampConverter(timestamp, "HH");

        return switch (convertedApiHour) {
            case "00" -> "00:00";
            case "01", "02" -> "02:00";
            case "03" -> "03:00";
            case "04", "05" -> "05:00";
            case "06" -> "06:00";
            case "07", "08" -> "08:00";
            case "09" -> "09:00";
            case "10", "11" -> "11:00";
            case "12" -> "12:00";
            case "13", "14" -> "14:00";
            case "15" -> "15:00";
            case "16", "17" -> "17:00";
            case "18" -> "18:00";
            case "19", "20" -> "20:00";
            case "21" -> "21:00";
            default -> "23:00";
        };
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

    public String rainProbabilityConverter(double pop, Map<BotText.TextName, String> text) {
        var percentage = (int) (pop * 100);
        return (percentage <= 50) ? percentage + "%." : percentage + text.get(BotText.TextName.FORECAST_UMBRELLA_WARNING);
    }

}