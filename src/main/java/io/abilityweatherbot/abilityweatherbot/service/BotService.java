package io.abilityweatherbot.abilityweatherbot.service;

import io.abilityweatherbot.abilityweatherbot.config.OpenWeatherConfig;
import io.abilityweatherbot.abilityweatherbot.dto.CityDto;
import io.abilityweatherbot.abilityweatherbot.dto.WeatherDto;
import io.abilityweatherbot.abilityweatherbot.dto.forecast.ForecastDetails;
import io.abilityweatherbot.abilityweatherbot.dto.forecast.ForecastDto;
import io.abilityweatherbot.abilityweatherbot.util.Converter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Queue;

@Log4j2
@Component
@PropertySource("/application.properties")
public class BotService {

    @Value("${openweather.city_url}")
    private String cityUrl;

    @Value("${openweather.weather_url}")
    private String weatherUrl;

    @Value("${openweather.forecast_url}")
    private String forecastUrl;

    private final RestTemplate restTemplate;
    private final OpenWeatherConfig openWeatherConfig;
    private final Converter converter;
    private final DecimalFormat decimalFormat;
    private final MessageHandler messageHandler;

    @Autowired
    public BotService(RestTemplate restTemplate,
                      OpenWeatherConfig openWeatherConfig,
                      Converter converter,
                      DecimalFormat decimalFormat,
                      MessageHandler messageHandler) {
        this.restTemplate = restTemplate;
        this.openWeatherConfig = openWeatherConfig;
        this.converter = converter;
        this.decimalFormat = decimalFormat;
        this.messageHandler = messageHandler;
    }

    public SendMessage displayWeather(Update update, String cityName, boolean hasRecentCitiesKeyboard, Queue<String> queueOfCities) {
        var wrongCityMessage = messageHandler.makeMessage(update, "❌There is no city with this name. Try again please");
        var cityUrlQuery = cityUrl + cityName + "&limit=5&appid=";
        var message = new SendMessage();

        ResponseEntity<List<CityDto>> cityResponse = restTemplate.exchange(cityUrlQuery + openWeatherConfig.getToken(),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        List<CityDto> cityDtoList = cityResponse.getBody();

        if (cityDtoList != null && !cityDtoList.isEmpty()) {

            var weatherDto = restTemplate.getForObject(weatherUrl + "lat=" +
                    cityDtoList.get(0).getLat() + "&lon=" + cityDtoList.get(0).getLon() +
                    "&appid=" + openWeatherConfig.getToken() + "&units=metric", WeatherDto.class);

            var messageString = String.format("\uD83D\uDCC6 %s \n%s %s °C, %s. \n\uD83D\uDCA7 Humidity: %s%%. \n\uD83D\uDCA8 Wind speed: %sm/s\n\n",
                    converter.dateConverter(weatherDto.getDt(), "dd.MM"),
                    converter.stringToEmojiConverter(weatherDto.getWeatherInfo().get(0).get("main").toString()),
                    decimalFormat.format(weatherDto.getMainInfo().get("temp")),
                    weatherDto.getWeatherInfo().get(0).get("description"),
                    weatherDto.getMainInfo().get("humidity"),
                    weatherDto.getWindInfo().get("speed"));

            if (hasRecentCitiesKeyboard) {
                message.setReplyMarkup(messageHandler.recentCitiesMarkup(queueOfCities));
            }

            message.setChatId(update.getMessage().getChatId().toString());
            message.setText("Weather in " + cityName + "\n\n" + messageString);

            log.info("Single weather query '{}' in {} chat id", cityName, update.getMessage().getChatId());
            return message;
        }

        log.info("Error in chat by id {} while querying single weather in '{}' city.",
                update.getMessage().getChatId(), cityName);
        return wrongCityMessage;
    }


    public SendMessage displayWeatherForecast(Update update, String cityName) {
        var wrongCityMessage = messageHandler.makeMessage(update, "❌There is no city with this name. Try again please");
        var cityUrlQuery = cityUrl + cityName + "&limit=5&appid=";
        var message = new SendMessage();

        ResponseEntity<List<CityDto>> cityResponse = restTemplate.exchange(cityUrlQuery + openWeatherConfig.getToken(),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        List<CityDto> cityDtoList = cityResponse.getBody();


        if (cityDtoList != null && !cityDtoList.isEmpty()) {

            // 'cnt' - to limit number of timestamps
            // 'lang' - for language selection
            var forecastDto = restTemplate.getForObject(forecastUrl + "lat=" +
                    cityDtoList.get(0).getLat() + "&lon=" + cityDtoList.get(0).getLon() +
                    "&appid=" + openWeatherConfig.getToken() + "&units=metric", ForecastDto.class);

            if (forecastDto != null) {
                List<ForecastDetails> forecastDetailsList = forecastDto.getForecastDetailsList();

                var messageStringBuilder = new StringBuilder();

                for (ForecastDetails forecastDetails : forecastDetailsList) {
                    if (converter.dateConverter(forecastDetails.getDt(), "HH:mm").equals("14:00")) {
                        messageStringBuilder
                                .append("\uD83D\uDCC6 ")
                                .append(converter.dateConverter(forecastDetails.getDt(), "dd.MM"))
                                .append("\n")
                                .append(converter.stringToEmojiConverter(forecastDetails.getWeather().get(0).get("main").toString()))
                                .append(" ")
                                .append(decimalFormat.format(forecastDetails.getMain().get("temp"))).append("°C,")
                                .append(" ")
                                .append(forecastDetails.getWeather().get(0).get("description")).append(". ")
                                .append("\n")
                                .append("☂").append(" Rainfall probability: ").append(converter.rainProbabilityConverter(forecastDetails.getRainfallProbability()))
                                .append("\n")
                                .append("\uD83D\uDCA7").append(" Humidity: ").append(forecastDetails.getMain().get("humidity")).append("%.")
                                .append("\n")
                                .append("\uD83D\uDCA8").append(" Wind speed: ").append(forecastDetails.getWind().get("speed")).append("m/s")
                                .append("\n\n");

                        message.setChatId(update.getMessage().getChatId().toString());
                        message.setText("Forecast for " + cityName + " at 14:00: " + "\n\n" + messageStringBuilder);

                    }
                }

                log.info("Weather forecast query '{}' in {} chat id.", cityName, update.getMessage().getChatId());
                return message;
            }
        }

        log.info("Error in chat by id {} while querying forecast in '{}' city.", update.getMessage().getChatId(), cityName);
        return wrongCityMessage;
    }
}
