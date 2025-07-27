package com.telegrambot.kocherovbot.service;

import com.telegrambot.kocherovbot.BotConfiguration;
import com.telegrambot.kocherovbot.controller.ChatGptController;
import com.telegrambot.kocherovbot.repository.RedisChatRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Log4j2
public class BotService {
    @Value("${openai.conditions.comment.before}")
    private String promptPartBeforeComment;
    @Value("${openai.conditions.comment.after}")
    private String promptPartAfterComment;
    @Value("${openai.conditions.post.before}")
    private String promptPartBeforePost;
    @Value("${openai.conditions.post.after}")
    private String promptPartAfterPost;

    private final ChatGptController chatGptController;
    private final RedisChatRepositoryImpl chatsRepository;

    private final ArrayList<Long> discussionGroupIds = new ArrayList<>();
    @Value("${openai.conditions.chance}")
    private double randomChance;

    public SendMessage handleMessage(Update update) {
        if (update.getMessage() != null && update.getMessage().getText() != null) {
            var chatId = update.getMessage().getChatId();
            log.info("Новый update из группы: " + chatId);
            log.info(update);

            if (isReply(update) || isMentionMessage(update)) {
                log.info("В чате " + chatId + " новое сообщение с упоминанием от " + update.getMessage().getFrom().getId() + ": " + update.getMessage().getText());
                chatsRepository.saveMessage(update.getMessage());
                return sendReplyToComment(update);

            } else if (update.getMessage().getFrom() != null &&
                update.getMessage().getMessageThreadId() != null &&
                isRandomChanceMessage(update)) {
                log.info("В чате " + chatId + " новое сообщение от " + update.getMessage().getFrom().getId() + ": " + update.getMessage().getText());
                chatsRepository.saveMessage(update.getMessage());
                return sendReplyToComment(update);

            } else if (discussionGroupIds.contains(update.getMessage().getChatId()) &&
                update.getMessage().getMessageThreadId() == null &&
                (update.getMessage().getFrom().getFirstName().equals("Telegram") ||
                update.getMessage().getFrom().getFirstName().equals("Group"))) {
                log.info("В супергруппе " + chatId + " новый пост из канала: " + update.getMessage().getText());
                chatsRepository.savePost(update.getMessage());
                return sendPostComment(update);
            }

        } else if ((update.getChannelPost() != null && update.getChannelPost().isChannelMessage())) {
            if (!discussionGroupIds.contains(update.getChannelPost().getChat().getLinkedChatId())) {
                discussionGroupIds.add(update.getChannelPost().getChat().getLinkedChatId());
                log.info("Добавлен новый канал: " + update.getChannelPost().getChatId() + ", его супергруппа: " + update.getChannelPost().getChat().getLinkedChatId());
            }
        }

        return null;
    }

    public SendMessage sendReplyToComment(Update update) {
        var context = chatsRepository.findMessagesByThreadId(update.getMessage().getMessageThreadId().toString());
        log.info("Контекст который получил бот:" + context);

        var answer = chatGptController.getGptAnswer(
            promptPartBeforeComment + "\n" +
                context + "\n" +
                promptPartAfterComment);

        answer = validateAnswer(answer);

        return SendMessage.builder()
            .replyToMessageId(update.getMessage().getMessageId())
            .chatId(update.getMessage().getChat().getId())
            .text(answer)
            .build();
    }

    public SendMessage sendPostComment(Update update) {

        var answer = chatGptController.getGptAnswer(
            promptPartBeforePost + "\n" +
                update.getMessage().getText() + "\n" +
                promptPartAfterPost);

        answer = validateAnswer(answer);

        return SendMessage.builder()
            .chatId(update.getMessage().getChatId())
            .text(answer)
            .replyToMessageId(update.getMessage().getMessageId())
            .build();
    }

    private static String validateAnswer(String answer) {
        // Проверка нет ли в ответе собственного имени бота
        if (answer.matches("^.{1,9}:.*")) {
            answer = answer.replace(BotConfiguration.botUserName + ": ", "");
            answer = answer.replace("Комментарий: ", "");
        }

        return answer;
    }

    private boolean isFromBot(Update update) {
        try {
            return update.getMessage().getFrom().getIsBot();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isReply(Update update) {
        try {
            return update.getMessage().getReplyToMessage().getFrom().getUserName().equals(BotConfiguration.botName);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isRandomChanceMessage(Update update) {
        try {
            var random = Math.random();
            log.info(random);

            return random < randomChance;
        } catch (Exception e) {
            log.error("Math.random() error");
            log.error(randomChance);
            return false;
        }
    }

    private boolean isMentionMessage(Update update) {
        try {
            return update.getMessage().getText().contains("@" + BotConfiguration.botName);
        } catch (Exception e) {
            return false;
        }
    }
}
