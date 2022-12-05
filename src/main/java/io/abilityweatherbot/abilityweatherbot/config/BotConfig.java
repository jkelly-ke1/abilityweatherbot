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
public class BotConfig {

    @Value("${testbot.name}")
    private String botName;

    @Value("${testbot.token}")
    private String botToken;

    public BotConfig(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

}
