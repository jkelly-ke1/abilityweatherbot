package io.abilityweatherbot.weatherbot.service;

import io.abilityweatherbot.weatherbot.config.BotConfig;
import io.abilityweatherbot.weatherbot.dto.MenuStatus;
import io.abilityweatherbot.weatherbot.dto.UserSettingsDto;
import io.abilityweatherbot.weatherbot.models.UserSettings;
import io.abilityweatherbot.weatherbot.util.BotText;
import io.abilityweatherbot.weatherbot.util.MessageHandler;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static io.abilityweatherbot.weatherbot.dto.MenuStatus.*;
import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Log4j2
@PropertySource("/application.properties")
@Component
public class Bot extends AbilityBot {

    private final Update update = new Update();

    private final WeatherService weatherService;
    private final MessageHandler messageHandler;
    private final UserSettingsService userSettingsService;
    private final ModelMapper modelMapper;

    @Value("${telegram.creator_id}")
    private long creatorId;

    private final Map<Long, MenuStatus> userMenuState = new HashMap<>();

    // contains recent cities searches for each user
    private final Map<Long, Queue<String>> recentCities = new HashMap<>();

    private final Map<Long, Timer> userScheduleTimerMap = new HashMap<>();

    private final Map<Long, TimerTask> userTimerTaskMap = new HashMap<>();

    private static final Map<Long, Map<BotText.TextName, String>> botText = new HashMap<>();

    private static final String regexCityPattern = "[\\p{L}.-]+";


    @Autowired
    public Bot(BotConfig botConfig,
               WeatherService weatherService,

               MessageHandler messageHandler, UserSettingsService userSettingsService, ModelMapper modelMapper) {
        super(botConfig.getBotToken(), botConfig.getBotToken());
        this.weatherService = weatherService;
        this.messageHandler = messageHandler;

        this.userSettingsService = userSettingsService;
        this.modelMapper = modelMapper;
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
                .action(act -> {
                    userSettingsService.addUser(onInitUserSettingMap(act.chatId(), act.user().getLanguageCode()));
                    botText.put(userSettingsService.getUserSettingsByTelegramUserId(act.user().getId()).getTelegramUserId(),
                            BotText.setupLanguageByDefaultLanguageCode(userSettingsService.getUserSettingsByTelegramUserId(act.user().getId()).getLanguageCode()));
                    silent.send(botText.get(act.user().getId()).get(BotText.TextName.START), act.chatId());
                    userMenuState.put(act.chatId(), START);
                })
                .build();
    }

    public Ability weather() {
        return Ability.builder()
                .name("weather")
                .info("check current weather in your city")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(act -> {
                    silent.send(botText.get(act.user().getId()).get(BotText.TextName.ENTRY), act.chatId());
                    userMenuState.put(act.chatId(), CURRENT_WEATHER);
                })
                .build();
    }

    public Ability schedule() {
        return Ability.builder()
                .name("schedule")
                .info("get current weather every couple hours")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(act -> {
                    silent.send(botText.get(act.user().getId()).get(BotText.TextName.ENTRY), act.chatId());
                    userMenuState.put(act.chatId(), SCHEDULE_WEATHER);
                })
                .build();
    }

    public Ability forecast() {
        return Ability.builder()
                .name("forecast")
                .info("weather forecast for 5 days")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(act -> {
                    silent.send(botText.get(act.user().getId()).get(BotText.TextName.ENTRY), act.chatId());
                    userMenuState.put(act.chatId(), FORECAST_WEATHER);
                })
                .build();
    }

    public Ability cancel() {
        return Ability.builder()
                .name("cancel")
                .info("cancel current action")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(act -> displayWeatherByTimer(update, false, null, 0))
                .build();
    }


    public Ability language() {
        return Ability.builder()
                .name("language")
                .info("change bot language")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(act -> {
                    try {
                        execute(messageHandler.makeMessageWithLanguageMarkup(act.update(),
                                botText.get(act.user().getId()).get(BotText.TextName.SETTINGS_LANGUAGE_ENTRY)));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    userMenuState.put(act.chatId(), SETTINGS);
                })
                .build();
    }

    public Ability about() {
        return Ability.builder()
                .name("about")
                .info("about this bot")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(act -> silent.send(botText.get(act.user().getId()).get(BotText.TextName.ABOUT), act.chatId()))
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
                    menuHandler(act.update(), act.update().getMessage().getText(), userMenuState.get(act.chatId()), act.chatId());
                    update.setMessage(act.update().getMessage());
                })
                .build();
    }

    @SneakyThrows
    private void menuHandler(Update update, String incomingMessage, MenuStatus menuStatus, long chatId) {

        if (menuStatus == SETTINGS) {
            var updatedUserSettingsDto =
                    convertToUserSettingsDto(userSettingsService
                            .getUserSettingsByTelegramUserId(update.getMessage().getFrom().getId()));

            switch (incomingMessage) {
                case "English\uD83C\uDDEC\uD83C\uDDE7" -> {
                    botText.put(update.getMessage().getFrom().getId(), BotText.englishTextMap());
                    updatedUserSettingsDto.setBotLanguageCode("en");
                    userSettingsService.updateUserSettingsByTelegramUserId(update.getMessage().getFrom().getId(),
                            convertFromUserSettingsDto(updatedUserSettingsDto));
                    execute(messageHandler.makeMessage(update,
                            botText.get(update.getMessage().getFrom().getId())
                                    .get(BotText.TextName.SETTINGS_LANGUAGE_CHANGE_SUCCESS)));
                }
                case "Українська\uD83C\uDDFA\uD83C\uDDE6" -> {
                    botText.put(update.getMessage().getFrom().getId(), BotText.ukrainianTextMap());
                    updatedUserSettingsDto.setBotLanguageCode("uk");
                    userSettingsService.updateUserSettingsByTelegramUserId(update.getMessage().getFrom().getId(),
                            convertFromUserSettingsDto(updatedUserSettingsDto));
                    execute(messageHandler.makeMessage(update,
                            botText.get(update.getMessage().getFrom().getId())
                                    .get(BotText.TextName.SETTINGS_LANGUAGE_CHANGE_SUCCESS)));
                }
                case "Русский\uD83C\uDDF7\uD83C\uDDFA" -> {
                    botText.put(update.getMessage().getFrom().getId(), BotText.russianTextMap());
                    updatedUserSettingsDto.setBotLanguageCode("ru");
                    userSettingsService.updateUserSettingsByTelegramUserId(update.getMessage().getFrom().getId(),
                            convertFromUserSettingsDto(updatedUserSettingsDto));
                    execute(messageHandler.makeMessage(update,
                            botText.get(update.getMessage().getFrom().getId())
                                    .get(BotText.TextName.SETTINGS_LANGUAGE_CHANGE_SUCCESS)));
                }
            }

        } else if (incomingMessage.matches(regexCityPattern)) {

            if (!recentCities.containsKey(chatId))
                recentCities.put(chatId, new PriorityQueue<>(4));

            if (menuStatus == CURRENT_WEATHER) {
                recentCities.get(chatId).add(incomingMessage);
                execute(weatherService.displayWeather(update, incomingMessage, true,
                        recentCities.get(chatId), botText.get(update.getMessage().getFrom().getId()),
                        userSettingsService.getUserSettingsByTelegramUserId(update.getMessage().getFrom().getId())
                                .getBotLanguageCode()));
            }

            if (menuStatus == FORECAST_WEATHER) {
                recentCities.get(chatId).add(incomingMessage);
                execute(weatherService.displayWeatherForecast(update, incomingMessage,
                        botText.get(update.getMessage().getFrom().getId()),
                        userSettingsService.getUserSettingsByTelegramUserId(update.getMessage().getFrom().getId())
                                .getBotLanguageCode()));
            }

            // 1800000ms == 0.5hr, 10800000ms == 3hrs
            if (menuStatus == SCHEDULE_WEATHER) {
                userScheduleTimerMap.put(update.getMessage().getChatId(), new Timer());
                userMenuState.put(update.getMessage().getChatId(), START);
                displayWeatherByTimer(update, true, incomingMessage, 10800000);
            }

        } else {
            execute(messageHandler.makeMessage(update,
                    botText.get(update.getMessage().getFrom().getId()).get(BotText.TextName.WRONG)));
        }

    }

    @SneakyThrows
    private void displayWeatherByTimer(Update update, boolean isActive, String cityName, long period) {
        var timer = userScheduleTimerMap.get(update.getMessage().getChatId());
        var oldTimerTask = userTimerTaskMap.get(update.getMessage().getChatId());

        TimerTask timerTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                execute(weatherService.displayWeather(update, cityName, false,
                        null, botText.get(update.getMessage().getFrom().getId()),
                        userSettingsService.getUserSettingsByTelegramUserId(update.getMessage().getFrom().getId()).getBotLanguageCode()));
            }
        };

        if (isActive) {
            if (oldTimerTask != null)
                oldTimerTask.cancel();

            userTimerTaskMap.put(update.getMessage().getChatId(), timerTask);
            timer.schedule(userTimerTaskMap.get(update.getMessage().getChatId()), new Date(), period);
            log.info("Schedule query '{}' in {} chat id was started.",
                    cityName, update.getMessage().getChatId());
        } else {
            oldTimerTask.cancel();
            execute(messageHandler.makeMessage(update,
                    botText.get(update.getMessage().getFrom().getId()).get(BotText.TextName.SCHEDULE_CANCEL)));
            log.info("Schedule in chat by id {} was canceled", update.getMessage().getChatId());
        }
    }

    private UserSettings convertFromUserSettingsDto (UserSettingsDto userSettingsDto) {
        return modelMapper.map(userSettingsDto, UserSettings.class);
    }

    private UserSettingsDto convertToUserSettingsDto (UserSettings userSettings) {
        return modelMapper.map(userSettings, UserSettingsDto.class);
    }

    // call this method once for every user when '/start' command was send
    private UserSettings onInitUserSettingMap(long userId, String languageCode) {
        var userSettings = new UserSettings();
        userSettings.setTelegramUserId(userId);
        userSettings.setLanguageCode(languageCode);
        userSettings.setBotLanguageCode(languageCode);
        return userSettings;
    }

}