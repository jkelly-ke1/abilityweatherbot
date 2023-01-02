package io.abilityweatherbot.abilityweatherbot.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class UserSettingsDto {

    @Column(name = "bot_language_code")
    private String botLanguageCode;

}
