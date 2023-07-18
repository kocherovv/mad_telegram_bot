package com.telegrambot.kocherovbot.service;

import com.telegrambot.kocherovbot.controller.ChatGptController;
import com.telegrambot.kocherovbot.domen.BotCondition;
import com.telegrambot.kocherovbot.domen.DialogMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;

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
    @Getter @Setter
    private BotCondition answerCondition = BotCondition.builder()
        .after(answerAfter)
        .before(answerBefore)
        .build();
    @Getter @Setter
    private BotCondition commentCondition = BotCondition.builder()
        .after(commentAfter)
        .before(commentBefore)
        .build();

    public SendMessage answerWithContext(Long chatId, Integer updateId, ArrayList<DialogMessage> dialog) {
        var dialogMessages = dialog.stream()
            .filter(e -> e.getChatId().equals(chatId))
            .toList();

        var answer = chatGptController.chat(
            answerCondition.getBefore() + dialogMessages + answerCondition.getAfter());

        answer = checkSelfNaming(answer);

        return SendMessage.builder()
            .replyToMessageId(updateId)
            .chatId(chatId)
            .text(answer)
            .build();
    }

    public SendMessage comment(Long chatId, Integer updateId, ArrayList<DialogMessage> dialog) {
        var dialogMessages = dialog.stream()
            .filter(e -> e.getChatId().equals(chatId))
            .toList();

        var answer = chatGptController.chat(
            commentCondition.getBefore() + dialogMessages + commentCondition.getAfter());

        answer = checkSelfNaming(answer);

        return SendMessage.builder()
            .replyToMessageId(updateId)
            .chatId(chatId)
            .text(answer)
            .build();
    }

    public void setDefaultCondition() {
        answerCondition = BotCondition.builder()
            .after(answerAfter)
            .before(answerBefore)
            .build();
        commentCondition = BotCondition.builder()
            .after(commentAfter)
            .before(commentBefore)
            .build();
        answerBefore = null;
        answerAfter = null;
        commentBefore = null;
        commentAfter = null;
    }

    public void setWorkCondition() {
        answerCondition = BotCondition.builder()
            .after("Ответь на последнее сообщение диалога как буд-то ты Mad Robot:")
            .before("")
            .build();
        commentCondition = BotCondition.builder()
            .after("Пошути над последним сообщением в диалоге от имени Mad Robot: ")
            .before("\" - Сообщение должно быть коротким")
            .build();
        answerBefore = null;
        answerAfter = null;
        commentBefore = null;
        commentAfter = null;
    }

    private static String checkSelfNaming(String answer) {
        if (answer.matches("^.{1,9}:.*")) {
            answer = answer.replace("Mad Bot: ", "")
                .replace(System.getenv("BOT_USERNAME") + ": ", "");
        }

        return answer;
    }
}
