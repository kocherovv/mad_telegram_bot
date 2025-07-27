package com.telegrambot.kocherovbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BotMenuService {
    @Value("${openai.conditions.chance}")
    private double randomChance;

    public SendMessage menuOptionHandle(Update update) {
        var data = update.getCallbackQuery().getData();

        switch (data) {
            case "Отключить комментарии бота" -> {
                randomChance = 0;
                return SendMessage.builder()
                    .text("Комментарии отключены.randomChance = ".concat(String.valueOf(randomChance)))
                    .chatId(update.getCallbackQuery().getMessage().getChat().getId())
                    .build();
            }
            case "Настройки по умолчанию" -> {
                randomChance = 0.03;
                return SendMessage.builder()
                    .text("Мои настройки сброшены. randomChance = ".concat(String.valueOf(randomChance)))
                    .chatId(update.getCallbackQuery().getMessage().getChat().getId())
                    .build();
            }
        }
        return null;
    }

//    public SendMessage sendMenuOptions(Update update) {
//
//        return SendMessage.builder()
//            .chatId(update.getMessage().getChatId())
//            .text("Menu")
//            .replyMarkup(getMenuOptions())
//            .build();
//    }

    private InlineKeyboardMarkup getMenuOptions() {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var buttonsRow1 = new ArrayList<InlineKeyboardButton>();
        var buttonsRow2 = new ArrayList<InlineKeyboardButton>();

        buttonsRow1.add(InlineKeyboardButton.builder()
            .text("Отключить комментарии бота")
            .callbackData("Отключить комментарии бота")
            .build());
        buttonsRow2.add(InlineKeyboardButton.builder()
            .text("Настройки по умолчанию")
            .callbackData("Настройки по умолчанию")
            .build());

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(buttonsRow1);
        keyboardRows.add(buttonsRow2);

        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        return inlineKeyboardMarkup;
    }
}
