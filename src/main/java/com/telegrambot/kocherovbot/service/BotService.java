package com.telegrambot.kocherovbot.controller;

import com.telegrambot.kocherovbot.domen.DialogMessage;
import com.telegrambot.kocherovbot.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedList;

@Controller
@RequiredArgsConstructor
public class BotController {
    private final GptService gptService;

    public SendMessage handle(Update update, LinkedList<DialogMessage> dialogContext) {
        if (update.getMessage() == null || update.getMessage().getText() == null) {
            return null;
        }

        var isReplyToBot = (update.getMessage().getReplyToMessage() != null
            && update.getMessage().getReplyToMessage().getFrom().getUserName().equals("KocherovBot")
            && !update.getMessage().getFrom().getIsBot());

        var isFromPersonAndChanceTrue = Math.random() < 0.05 && !update.getMessage().getFrom().getIsBot();

        if (isReplyToBot) {
            return gptService.answerWithContext(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId(),
                update.getMessage().getText(),
                dialogContext);

        } else if (update.getMessage().getText().contains("@KocherovBot")) {
            return gptService.answerWithContext(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId(),
                update.getMessage().getText(),
                dialogContext);

        } else if (isFromPersonAndChanceTrue) {
            return gptService.comment(
                update.getMessage().getChat().getId(),
                update.getMessage().getMessageId(),
                update.getMessage().getText());
        }

        System.out.println("ПРОПУСК");
        return null;
    }
}
