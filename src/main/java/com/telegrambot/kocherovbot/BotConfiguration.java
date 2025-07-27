package com.telegrambot.kocherovbot;

import com.telegrambot.kocherovbot.repository.ChatsRepository;
import com.telegrambot.kocherovbot.service.BotMenuService;
import com.telegrambot.kocherovbot.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class BotConfiguration extends TelegramLongPollingBot {
    public static final String botUserName = System.getenv("BOT_USERNAME");
    public static final String botName = System.getenv("BOT_NAME");
    private final String botToken = System.getenv("BOT_TOKEN");

    private final BotService botService;
    private final ChatsRepository chatsRedisRepository;


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            if (update.getMessage() != null) {
//            var isAdmin = update.getMessage().getFrom().getId().toString().equals(adminId);
//            var isButtonOption = update.getCallbackQuery() != null;
//            var isSettingCommand = update.getMessage().getText().startsWith("/setting");
//
//            if (isAdmin) {
//                if (isButtonOption) {
//                    send(botMenuService.menuOptionHandle(update), update);
//
//                } else if (isSettingCommand) {
//                    send(botMenuService.sendMenuOptions(update), update);
//
//                } else {
//                    send(botService.handleMessage(update), update);
//                }
//            }

                send(botService.handleMessage(update));

            } else if (update.getChannelPost() != null) {
                var chatRequest = GetChat.builder()
                    .chatId(update.getChannelPost().getChatId())
                    .build();
                var chat = getChatInfo(chatRequest);
                update.getChannelPost().getChat().setLinkedChatId(chat.getLinkedChatId());
                send(botService.handleMessage(update));
            }
        }
    }

    private void send(SendMessage answer) {
        if (answer != null) {
            try {
                var message = sendApiMethod(answer);
                chatsRedisRepository.saveMessage(message);

                log.info("Бот ответил в чате " + answer.getChatId() + ": " + answer.getText());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("No answer");
        }
    }

    public Chat getChatInfo(GetChat answer) {
        if (answer != null) {
            try {
                return sendApiMethod(answer);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
