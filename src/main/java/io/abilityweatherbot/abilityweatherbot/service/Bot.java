package io.abilityweatherbot.abilityweatherbot.service;

import io.abilityweatherbot.abilityweatherbot.config.BotConfig;
import io.abilityweatherbot.abilityweatherbot.dto.MenuStatus;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

import static io.abilityweatherbot.abilityweatherbot.dto.MenuStatus.*;
import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Log4j2
@PropertySource("/application.properties")
@Component
public class Bot extends AbilityBot {

    private final Update update = new Update();

    private final BotService botService;
    private final MessageHandler messageHandler;

    @Value("${telegram.creator_id}")
    private long creatorId;

    private final Map<Long, MenuStatus> userState = new HashMap<>();

    // user's recent cities search
    private final Map<Long, Queue<String>> recentCities = new HashMap<>();

    private static final String entryMessage = "Enter city name\uD83D\uDC47";
    private static final String regexCityPattern = "^[A-Za-z.-]+$";

    @Autowired
    public Bot(BotConfig botConfig, BotService botService, MessageHandler messageHandler) {
        super(botConfig.getBotToken(), botConfig.getBotToken());
        this.botService = botService;
        this.messageHandler = messageHandler;
    }

    @Override
    public long creatorId() {
        return this.creatorId;
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

    public Ability cancel() {
        return Ability.builder()
                .name("cancel")
                .info("cancel current action")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(a -> {
                    userState.put(a.chatId(), START);
                    displayWeatherByTimer(update, false, null, 0);
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

    // all non-command messages listener
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
                })
                .build();
    }

    @SneakyThrows
    private void displayWeather(Update update, String cityName, MenuStatus menuStatus, long chatId) {
        if (cityName.matches(regexCityPattern)) {
            if (!recentCities.containsKey(chatId))
                recentCities.put(chatId, new PriorityQueue<>(4));

            if (menuStatus == CURRENT_WEATHER) {
                recentCities.get(chatId).add(cityName);
                execute(botService.displayWeather(update, cityName, true, recentCities.get(chatId)));
            }

            if (menuStatus == FORECAST_WEATHER) {
                recentCities.get(chatId).add(cityName);
                execute(botService.displayWeatherForecast(update, cityName));
            }

            // 1800000ms == 0.5hr, 10800000ms == 3hrs
            if (menuStatus == SCHEDULE_WEATHER) {
                displayWeatherByTimer(update, true, cityName, 10800000);
            }

        } else {
            execute(messageHandler.makeMessage(update, "❌Wrong city name format!"));
        }
    }

    @SneakyThrows
    private void displayWeatherByTimer(Update update, boolean isActive, String cityName, long period) {
        Timer timer = new Timer();

        if (isActive) {
            TimerTask timerTask = new TimerTask() {
                @SneakyThrows
                @Override
                public void run() {
                    execute(botService.displayWeather(update, cityName, false, null));
                }
            };

            timer.schedule(timerTask, new Date(), period);
            log.info("Schedule query '{}' in {} chat id was started.",
                    cityName, update.getMessage().getChatId());

        } else {
            timer.cancel();
            execute(messageHandler.makeMessage(update, "✅Schedule is canceled."));
            log.info("Schedule in chat by id {} was canceled", update.getMessage().getChatId());
        }
    }

}