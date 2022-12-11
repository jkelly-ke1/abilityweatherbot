package io.abilityweatherbot.abilityweatherbot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Component
public class MessageHandler {

    public ReplyKeyboardMarkup recentCitiesMarkup(Queue<String> queueOfCities) {
        var markup = new ReplyKeyboardMarkup();
        var row = new KeyboardRow();
        List<KeyboardRow> keyboard = new ArrayList<>();

        if (queueOfCities.size() > 4)
            queueOfCities.remove();

        Set<String> distinctArrayListOfCities = new HashSet<>(queueOfCities);

        for (String city : distinctArrayListOfCities)
            row.add(city);

        keyboard.add(row);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(keyboard);
        return markup;
    }

    public SendMessage makeMessage(Update update, String inputMessage) {
        var messageToSend = new SendMessage();
        messageToSend.setChatId(update.getMessage().getChatId());
        messageToSend.setText(inputMessage);

        return messageToSend;
    }

}
