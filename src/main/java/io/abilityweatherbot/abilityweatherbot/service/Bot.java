package io.abilityweatherbot.abilityweatherbot.service;

import io.abilityweatherbot.abilityweatherbot.config.BotConfig;
import io.abilityweatherbot.abilityweatherbot.dto.MenuStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static io.abilityweatherbot.abilityweatherbot.dto.MenuStatus.*;
import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Log4j2
@Component
@PropertySource("/application.properties")
public class Bot extends AbilityBot {

    private final Update update = new Update();
    private final Map<Long, MenuStatus> userState = new HashMap<>();
    private final String regexCityPattern = "^[A-Za-z.-]+$";
    private String entryMessage = "Enter city name\uD83D\uDC47";
    private final BotService botService;
    private final MessageHandler messageHandler;

    @Autowired
    public Bot(BotConfig botConfig, BotService botService, MessageHandler messageHandler) {
        super(botConfig.getBotToken(), botConfig.getBotToken());

        this.botService = botService;
        this.messageHandler = messageHandler;
    }

    // TODO: 21.11.2022 handle before push
    @Override
    public long creatorId() {
        return 0;
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info("(re)start bot")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> {
                    silent.send("""
                            Hello!✋
                            I am @neatWeatherBot 🤖
                            You can check weather by typing /weather command. 
                            There are another features too! 
                            Just press menu button 👇 
                            """, a.chatId());
                    userState.put(a.chatId(), START);
                    System.out.println(userState.toString());
                })
                .build();
    }

    public Ability weather() {
        return Ability.builder()
                .name("weather")
                .info("check current weather in your city")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> {
                    silent.send(entryMessage, a.chatId());
                    userState.put(a.chatId(), CURRENT_WEATHER);
                })
                .build();
    }

    public Ability schedule() {
        return Ability.builder()
                .name("schedule")
                .info("get current weather every couple hours")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> {
                    silent.send(entryMessage, a.chatId());
                    userState.put(a.chatId(), SCHEDULE_WEATHER);
                })
                .build();
    }

    public Ability forecast() {
        return Ability.builder()
                .name("forecast")
                .info("weather forecast for 5 days")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> {
                    silent.send(entryMessage, a.chatId());
                    userState.put(a.chatId(), FORECAST_WEATHER);
                })
                .build();
    }

    public Ability about() {
        return Ability.builder()
                .name("about")
                .info("about this bot")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> silent.send("""
                        It's a simple weather bot that allows you to check weather
                        from any place in the world. 
                        The information provided by 
                        the bot is based on meteorological 
                        data taken from openweathermap.com site.
                        For any further questions, author: @VelorumX 👌
                        """, a.chatId()))
                .build();
    }

    public Ability messageListener() {
        return Ability.builder()
                .name(DEFAULT)
                .flag(MESSAGE)
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(act -> {
                    displayWeather(act.update(), act.update().getMessage().getText(), userState.get(act.chatId()), act.chatId());
                    update.setMessage(act.update().getMessage());
//                    silent.send(displayWeather(act.update().getMessage().getText(), userState.get(act.chatId())), act.chatId());
//                    silent.send(messageHandler.sendMessage(act.update(), act.update().getMessage().getText()).toString(), act.chatId());
                })
                .build();
    }

    private void displayWeather(Update update, String cityName, MenuStatus menuStatus, long chatId) {
        if (cityName.matches(regexCityPattern)) {

            if(menuStatus == CURRENT_WEATHER)
                silent.send(botService.displayWeather(update, cityName, false, null).toString(), chatId);

            if(menuStatus == FORECAST_WEATHER)
                silent.send(botService.displayWeatherForecast(update, cityName).toString(), chatId);

            if (menuStatus == SCHEDULE_WEATHER)
                silent.send("Not available", chatId);

        } else {
            silent.send("not match to regex", chatId);
        }
    }

    private Predicate<Update> hasMessageWith(String msg) {
        return upd -> upd.getMessage().getText().matches(regexCityPattern);
    }

/*
        public Ability cancel() {
        return Ability.builder()
                .name("forecast")
                .info("weather forecast for 5 days")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> {
                    silent.send(entryMessage, a.chatId());
                    userState.put(a.chatId(), FORECAST_WEATHER);
                })
                .build();
    }*/


}
