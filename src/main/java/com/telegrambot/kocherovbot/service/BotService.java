package com.telegrambot.kocherovbot.service;

import com.telegrambot.kocherovbot.domen.DialogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedList;

@Controller
@RequiredArgsConstructor
public class BotService {
    private final GptService gptService;
    @Value("${openai.conditions.chance}")
    private double randomChance;

    public SendMessage handle(Update update, LinkedList<DialogMessage> dialogContext) {
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
                update.getMessage().getText());
        }

        System.out.println("ПРОПУСК");
        return null;
    }
}
