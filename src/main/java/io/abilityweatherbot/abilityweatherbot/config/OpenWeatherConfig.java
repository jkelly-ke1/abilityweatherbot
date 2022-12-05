package io.abilityweatherbot.abilityweatherbot.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@NoArgsConstructor
@Getter
@PropertySource("/application.properties")
public class OpenWeatherConfig {

    @Value("${openweather.token}")
    private String token;

}
