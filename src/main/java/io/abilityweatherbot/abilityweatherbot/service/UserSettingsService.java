package io.abilityweatherbot.abilityweatherbot.service;

import io.abilityweatherbot.abilityweatherbot.models.UserSettings;
import io.abilityweatherbot.abilityweatherbot.repositories.UserSettingsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
@Transactional(readOnly = true)
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    @Transactional
    public void addUser(UserSettings userSettings) {
        if (userSettingsRepository.getUserSettingsByTelegramUserId(userSettings.getTelegramUserId()).isEmpty()) {
            userSettingsRepository.save(userSettings);
            log.info("New by id '{}' was added.", userSettings.getTelegramUserId());
        }
    }

    public UserSettings getUserSettingsByTelegramUserId(long telegramId) {
        Optional<UserSettings> userSettings = userSettingsRepository.getUserSettingsByTelegramUserId(telegramId);
        return userSettings.orElseThrow();
    }

    @Transactional
    public void updateUserSettingsByTelegramUserId(long telegramUserId, UserSettings updatedUserSettings) {
        userSettingsRepository.getUserSettingsByTelegramUserId(telegramUserId)
                .ifPresent(user -> user.setBotLanguageCode(updatedUserSettings.getBotLanguageCode()));

    }


}
