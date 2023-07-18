package com.telegrambot.kocherovbot.conf;

import com.telegrambot.kocherovbot.domen.DialogMessage;
import com.telegrambot.kocherovbot.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class BotConfiguration extends TelegramLongPollingBot {
    private final String botName = System.getenv("BOT_NAME");
    private final String botToken = System.getenv("BOT_TOKEN");
    private final BotService botService;
    private final ArrayList<DialogMessage> dialogContext;

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
        if (update.getCallbackQuery() != null) {
            var message = botService.menuSwitch(update);
            send(message);

        } else if (update.getMessage().getText().startsWith("/setting")) {
            var menuButtons = botService.openMenu(update);
            send(menuButtons);

        } else {
            addMessageToContext(
                update.getMessage().getChat().getId(),
                update.getMessage().getFrom().getFirstName() + " "
                    + update.getMessage().getFrom().getLastName(),
                update.getMessage().getText()
            );
            var answer = botService.answerMessage(update, dialogContext);
            sendAnswer(update, answer);
        }
    }

    private void send(SendMessage answer) {
        if (answer != null) {
            try {
                sendApiMethod(answer);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendAnswer(Update update, SendMessage answer) {
        if (answer != null) {
            try {
                sendApiMethod(answer);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            addMessageToContext(update.getMessage().getChat().getId(),
                System.getenv("BOT_USERNAME"), answer.getText());

            log.info(dialogContext.stream()
                .filter(e -> e.getChatId().equals(update.getMessage().getChatId()))
                .toList());
        }
    }

    private void addMessageToContext(Long chatId, String name, String text) {
        var context = new ArrayList<>(dialogContext.stream()
            .filter(m -> m.getChatId().equals(chatId))
            .toList());

        if (context.size() == 10) {
            var old = context.remove(0);
            dialogContext.remove(old);
        }
        dialogContext.add(DialogMessage.builder()
            .chatId(chatId)
            .member(name)
            .content(text)
            .build());
    }
}
