package com.telegrambot.kocherovbot.service;

import com.telegrambot.kocherovbot.domen.DialogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class BotService {
    private final GptService gptService;
    @Value("${openai.conditions.chance}")
    private double randomChance;

    public SendMessage answerMessage(Update update, ArrayList<DialogMessage> dialogContext) {
        if (update == null || update.getMessage() == null || update.getMessage().getText() == null) {
            return null;
        }

        var isReplyToBot = (update.getMessage().getReplyToMessage() != null
            && update.getMessage().getReplyToMessage().getFrom().getUserName().equals("KocherovBot")
            && !update.getMessage().getFrom().getIsBot());

        var isFromPersonAndChanceTrue = Math.random() < randomChance && !update.getMessage().getFrom().getIsBot();

        if (isReplyToBot) {
            return gptService.answerWithContext(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId(),
                dialogContext);

        } else if (update.getMessage().getText().contains("@KocherovBot")) {
            return gptService.answerWithContext(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId(),
                dialogContext);

        } else if (isFromPersonAndChanceTrue) {
            return gptService.comment(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId(),
                dialogContext);
        }

        log.info("ПРОПУСК");
        return null;
    }

    public SendMessage menuSwitch(Update update) {
        var data = update.getCallbackQuery().getData();

        switch (data) {
            case "Отключить комментарии бота" -> {
                randomChance = 0;
                return SendMessage.builder()
                    .text("Комментарии отключены.")
                    .chatId(update.getCallbackQuery().getMessage().getChat().getId())
                    .build();
            }
            case "Дать боту кофе" -> {
                gptService.setWorkCondition();
                randomChance = 0.03;
                return SendMessage.builder()
                    .text("Другое дело...")
                    .chatId(update.getCallbackQuery().getMessage().getChat().getId())
                    .build();
            }
            case "Настройки по умолчанию" -> {
                gptService.setDefaultCondition();
                randomChance = 0.03;
                return SendMessage.builder()
                    .text("Мои настройки сброшены.")
                    .chatId(update.getCallbackQuery().getMessage().getChat().getId())
                    .build();
            }
        }
        return null;
    }

    public SendMessage openMenu(Update update) {

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text("Menu")
            .replyMarkup(initKeyboard())
            .build();
    }

    private InlineKeyboardMarkup initKeyboard() {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var buttonsRow1 = new ArrayList<InlineKeyboardButton>();
        var buttonsRow2 = new ArrayList<InlineKeyboardButton>();
        var buttonsRow3 = new ArrayList<InlineKeyboardButton>();

        buttonsRow1.add(InlineKeyboardButton.builder()
            .text("Отключить комментарии бота")
            .callbackData("Отключить комментарии бота")
            .build());
        buttonsRow2.add(InlineKeyboardButton.builder()
            .text("Дать боту кофе")
            .callbackData("Дать боту кофе")
            .build());
        buttonsRow3.add(InlineKeyboardButton.builder()
            .text("Настройки по умолчанию")
            .callbackData("Настройки по умолчанию")
            .build());

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(buttonsRow1);
        keyboardRows.add(buttonsRow2);
        keyboardRows.add(buttonsRow3);

        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        return inlineKeyboardMarkup;
    }
}
