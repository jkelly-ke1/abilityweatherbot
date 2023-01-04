package io.abilityweatherbot.weatherbot.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BotText {
    public static final String START_ENG = """
            Hello!✋
            I am @neatWeatherBot 🤖
            You can check weather by typing /weather command. 
            There are another features too! 
            Just press menu button 👇 
            """;

    public static final String ABOUT_ENG = """
            It's a simple weather bot that allows you to check weather
            from any place in the world. 
            The information provided by 
            the bot is based on meteorological 
            data taken from openweathermap.com site.
            For any further questions, author: @VelorumX 👌
            """;
    public static final String ENTRY_MESSAGE_ENG = "Enter city name\uD83D\uDC47";
    public static final String SCHEDULE_CANCEL_ENG = "✅Schedule is canceled.";
    public static final String WRONG_FORMAT_ENG = "❌Wrong city name format!";
    public static final String WRONG_CITY_NAME_ENG = "❌There is no city with this name. Try again please";
    public static final String HUMIDITY_ENG = "Humidity: ";
    public static final String WIND_ENG = "Wind speed: ";
    public static final String WEATHER_IN_ENG = "Weather in ";
    public static final String FORECAST_RAINFALL_PROBABILITY_ENG = " Rainfall probability: ";
    public static final String FORECAST_ENTRY_ENG = "Forecast for ";
    public static final String FORECAST_TIME_ENG = " at 14:00: ";
    public static final String FORECAST_UMBRELLA_WARNING_ENG = "%. Better take your umbrella!";
    public static final String SETTINGS_LANGUAGE_ENTRY_ENG = """
            Here you can change bot language.
            To do this, select the required from the list below.
            👇 👇 👇 
            """;
    public static final String SETTINGS_LANGUAGE_CHANGE_SUCCESS_ENG = "Language was changed to \uD83C\uDDEC\uD83C\uDDE7!";


    public static final String START_UA = """
            Привіт!✋
            Я @neatWeatherBot 🤖
            Для того щоб дізнатись погоду введіть команду /weather. 
            Є й інші функції!
            Просто нажміть кнопку меню 👇
            """;
    public static final String ABOUT_UA = """
            Це простий бот який дозволяє дізнатись погоду
            в будь-якій точці світу.
            Інформація, яку надає цей бот, базується на
            метеорологічних даних які взяті
            з сайту openweathermap.com.
            З будь-якими додатковими запитаннями, 
            звертайтесь до автора: @VelorumX 👌
            """;
    public static final String ENTRY_MESSAGE_UA = "Введіть назву міста\uD83D\uDC47";
    public static final String SCHEDULE_CANCEL_UA = "✅Погода по розкладу відключена.";
    public static final String WRONG_FORMAT_UA = "❌Неправильний формат міста!";
    public static final String WRONG_CITY_NAME_UA = "❌Населеного пункту з даним іменем не знайдено. Будь-ласка, спробуйте ще раз";
    public static final String HUMIDITY_UA = "Вологість: ";
    public static final String WIND_UA = "Швидкість вітру: ";
    public static final String WEATHER_IN_UA = "Погода в ";
    public static final String FORECAST_RAINFALL_PROBABILITY_UA = " Імовірність опадів: ";
    public static final String FORECAST_ENTRY_UA = "Прогноз погоди для міста ";
    public static final String FORECAST_TIME_UA = " на 14:00: ";
    public static final String FORECAST_UMBRELLA_WARNING_UA = "%. Краще захопіть парасольку!";
    public static final String SETTINGS_LANGUAGE_ENTRY_UA = """
            Тут ви можете змінити мову бота.
            Для цього, оберіть потрібну мову зі списку нижче.
            👇 👇 👇 
            """;
    public static final String SETTINGS_LANGUAGE_CHANGE_SUCCESS_UA = "Мова була змінена на \uD83C\uDDFA\uD83C\uDDE6!";

    public static final String START_RU = """
            Привет!✋
            Я @neatWeatherBot 🤖
            Что бы узнать погоду введите команду /weather.
            Также доступны и другие функции!
            Просто нажмите кнопку меню 👇
            """;
    public static final String ABOUT_RU = """
            Это простой бот который позволяет узнать 
            погоду в любой точке мира.
            Информация, которую предоставляет бот, базируется
            на метеорологических данных которые взяты
            с сайта openweatherbot.com.
            С любыми дополнительными вопросами
            обращайтесь к автору: @VelorumX 👌
            """;
    public static final String ENTRY_MESSAGE_RU = "Введите название города\uD83D\uDC47";
    public static final String SCHEDULE_CANCEL_RU = "✅Погода по расписанию отключена.";
    public static final String WRONG_FORMAT_RU = "❌Неверный формат города!";
    public static final String WRONG_CITY_NAME_RU = "❌Населенного пункта с данным именем не найдено. Пожалуйста, попробуйте еще раз";
    public static final String HUMIDITY_RU = "Влажность: ";
    public static final String WIND_RU = "Скорость ветра: ";
    public static final String WEATHER_IN_RU = "Погода в ";
    public static final String FORECAST_RAINFALL_PROBABILITY_RU = " Вероятность осадков: ";
    public static final String FORECAST_ENTRY_RU = "Прогноз погоды в городе ";
    public static final String FORECAST_TIME_RU = " на 14:00: ";
    public static final String FORECAST_UMBRELLA_WARNING_RU = "%. Лучше захватите зонтик!";
    public static final String SETTINGS_LANGUAGE_ENTRY_RU = """
            Здесь вы можете изменить язык.
            Что-бы это сделать, выберите язык из списка ниже.
            👇 👇 👇
            """;
    public static final String SETTINGS_LANGUAGE_CHANGE_SUCCESS_RU = "Язык был изменен на \uD83C\uDDF7\uD83C\uDDFA!";

    public enum TextName {
        START, ABOUT, ENTRY, SCHEDULE_CANCEL, WRONG, WRONG_CITY, HUMIDITY, WIND, WEATHER_IN, FORECAST_RAINFALL_PROBABILITY,
        FORECAST_ENTRY, FORECAST_TIME, FORECAST_UMBRELLA_WARNING, SETTINGS_LANGUAGE_ENTRY,
        SETTINGS_LANGUAGE_CHANGE_SUCCESS
    }

    public static HashMap<TextName, String> englishTextMap() {
        return getLanguageHashMap(START_ENG, ABOUT_ENG, ENTRY_MESSAGE_ENG, SCHEDULE_CANCEL_ENG, WRONG_FORMAT_ENG, WRONG_CITY_NAME_ENG,
                HUMIDITY_ENG, WIND_ENG, WEATHER_IN_ENG, FORECAST_RAINFALL_PROBABILITY_ENG, FORECAST_ENTRY_ENG,
                FORECAST_TIME_ENG, FORECAST_UMBRELLA_WARNING_ENG, SETTINGS_LANGUAGE_ENTRY_ENG,
                SETTINGS_LANGUAGE_CHANGE_SUCCESS_ENG);
    }

    public static HashMap<TextName, String> ukrainianTextMap() {
        return getLanguageHashMap(START_UA, ABOUT_UA, ENTRY_MESSAGE_UA, SCHEDULE_CANCEL_UA, WRONG_FORMAT_UA, WRONG_CITY_NAME_UA,
                HUMIDITY_UA, WIND_UA, WEATHER_IN_UA, FORECAST_RAINFALL_PROBABILITY_UA, FORECAST_ENTRY_UA,
                FORECAST_TIME_UA, FORECAST_UMBRELLA_WARNING_UA, SETTINGS_LANGUAGE_ENTRY_UA,
                SETTINGS_LANGUAGE_CHANGE_SUCCESS_UA);
    }

    public static HashMap<TextName, String> russianTextMap() {
        return getLanguageHashMap(START_RU, ABOUT_RU, ENTRY_MESSAGE_RU, SCHEDULE_CANCEL_RU, WRONG_FORMAT_RU, WRONG_CITY_NAME_RU,
                HUMIDITY_RU, WIND_RU, WEATHER_IN_RU, FORECAST_RAINFALL_PROBABILITY_RU, FORECAST_ENTRY_RU,
                FORECAST_TIME_RU, FORECAST_UMBRELLA_WARNING_RU, SETTINGS_LANGUAGE_ENTRY_RU,
                SETTINGS_LANGUAGE_CHANGE_SUCCESS_RU);
    }

    @NotNull
    private static HashMap<TextName, String> getLanguageHashMap(String start, String about, String entryMessage,
                                                                String scheduleCancel,
                                                                String wrongFormat, String wrongCityName,
                                                                String humidity, String wind, String weatherIn,
                                                                String rainfallProbability, String forecastEntry,
                                                                String forecastTime, String umbrellaWarning,
                                                                String settingsLanguageEntry,
                                                                String settingsLanguageChangeSuccess) {
        HashMap<TextName, String> mapToReturn = new HashMap<>();
        mapToReturn.put(TextName.START, start);
        mapToReturn.put(TextName.ABOUT, about);
        mapToReturn.put(TextName.ENTRY, entryMessage);
        mapToReturn.put(TextName.SCHEDULE_CANCEL, scheduleCancel);
        mapToReturn.put(TextName.WRONG, wrongFormat);
        mapToReturn.put(TextName.WRONG_CITY, wrongCityName);
        mapToReturn.put(TextName.HUMIDITY, humidity);
        mapToReturn.put(TextName.WIND, wind);
        mapToReturn.put(TextName.WEATHER_IN, weatherIn);
        mapToReturn.put(TextName.FORECAST_RAINFALL_PROBABILITY, rainfallProbability);
        mapToReturn.put(TextName.FORECAST_ENTRY, forecastEntry);
        mapToReturn.put(TextName.FORECAST_TIME, forecastTime);
        mapToReturn.put(TextName.FORECAST_UMBRELLA_WARNING, umbrellaWarning);
        mapToReturn.put(TextName.SETTINGS_LANGUAGE_ENTRY, settingsLanguageEntry);
        mapToReturn.put(TextName.SETTINGS_LANGUAGE_CHANGE_SUCCESS, settingsLanguageChangeSuccess);


        return mapToReturn;
    }

    public static HashMap<TextName, String> setupLanguageByDefaultLanguageCode(String languageCode) {

        return switch (languageCode) {
            case "uk" -> ukrainianTextMap();
            case "ru" -> russianTextMap();
            default -> englishTextMap();
        };
    }

}