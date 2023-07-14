package com.telegrambot.kocherovbot.service;

import com.telegrambot.kocherovbot.controller.ChatGptController;
import com.telegrambot.kocherovbot.domen.DialogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class BotAction {

    private final ChatGptController chatGptController;

    public SendMessage answer(Long chatId, Integer updateId, String text) {
        var answer = chatGptController.chat("Ответь на сообщение, как буд-то тебя все достали: " + text + "\"");

        return SendMessage.builder()
            .replyToMessageId(updateId)
            .chatId(chatId)
            .text(answer)
            .build();
    }

    public SendMessage answerWithContext(Long chatId, Integer updateId, String text, LinkedList<DialogMessage> dialog) {
        var dialogMessages = dialog.stream().filter(e -> e.getChatId().equals(chatId)).toList();
        var answer = chatGptController.chat(
            "ответь на последнее сообщение диалога как буд-то ты Mad Robot, и тебя все достали: " + dialogMessages);

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
                .replace("Mad Robot: ", "");
        }
        return answer;
    }

    public SendMessage comment(Long chatId, Integer updateId, String text) {
        var answer = chatGptController.chat("Ответь на сообщение, как буд-то тебя все достали: " + text + "\"");

        return SendMessage.builder()
            .replyToMessageId(updateId)
            .chatId(chatId)
            .text(answer)
            .build();
    }
}
