package com.telegrambot.kocherovbot.service;

import com.telegrambot.kocherovbot.controller.ChatGptController;
import com.telegrambot.kocherovbot.domen.BotCondition;
import com.telegrambot.kocherovbot.domen.DialogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class GptService {
    private final ChatGptController chatGptController;

    @Value("${openai.conditions.answer.before}")
    private String answerBefore;
    @Value("${openai.conditions.answer.after}")
    private String answerAfter;
    @Value("${openai.conditions.comment.before}")
    private String commentBefore;
    @Value("${openai.conditions.comment.after}")
    private String commentAfter;

    public SendMessage answerWithContext(Long chatId, Integer updateId, LinkedList<DialogMessage> dialog) {
        var dialogMessages = dialog.stream().filter(e -> e.getChatId().equals(chatId)).toList();

        var answerCondition = BotCondition.builder()
            .after(answerAfter)
            .before(answerBefore)
            .build();

        var answer = chatGptController.chat(
            answerCondition.getBefore() + dialogMessages + answerCondition.getAfter());

        answer = checkSelfNaming(answer);

        return SendMessage.builder()
            .replyToMessageId(updateId)
            .chatId(chatId)
            .text(answer)
            .build();
    }

    public SendMessage comment(Long chatId, Integer updateId, LinkedList<DialogMessage> dialog) {
        var dialogMessages = dialog.stream().filter(e -> e.getChatId().equals(chatId)).toList();

        var commentCondition = BotCondition.builder()
            .after(commentAfter)
            .before(commentBefore)
            .build();

        var answer = chatGptController.chat(
            commentCondition.getBefore() + dialogMessages + commentCondition.getAfter());

        answer = checkSelfNaming(answer);

        return SendMessage.builder()
            .replyToMessageId(updateId)
            .chatId(chatId)
            .text(answer)
            .build();
    }

    private static String checkSelfNaming(String answer) {
        if (answer.matches("^.{1,9}:.*")) {
            answer = answer.replace("Mad Bot: ", "")
                .replace(System.getenv("BOT_USERNAME") + ": ", "");
        }
        return answer;
    }
}
