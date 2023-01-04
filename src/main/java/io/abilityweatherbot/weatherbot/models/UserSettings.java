package io.abilityweatherbot.weatherbot.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "telegram_user_id")
    private long telegramUserId;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "bot_language_code")
    private String botLanguageCode;

}



