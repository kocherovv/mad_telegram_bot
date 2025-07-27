package com.telegrambot.kocherovbot.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Log4j2
public class RedisChatRepositoryImpl implements ChatsRepository<Message> {

    private final RedisTemplate redisTemplate;

    @Override
    public Message saveMessage(Message entity) {
        var msg = concatSenderAndText(entity);
        var key = entity.getMessageThreadId().toString();
        redisTemplate.opsForList().rightPush(key, msg);
        log.info("В redis сохранено новое сообщение из обсуждения: " + msg + " | КЛЮЧ: " + key);
        return entity;
    }

    @Override
    public Message savePost(Message entity) {
        var msg = concatSenderAndText(entity);
        var key = entity.getMessageId().toString();
        if (entity.getMessageThreadId() == null) {
            redisTemplate.opsForList().rightPush(key, msg);
            log.info("В redis сохранено новое сообщение из канала: " + msg + " | КЛЮЧ: " + key);
        }
        return entity;
    }

    @Override
    public List<String> findMessagesByThreadId(String messageThreadId) {
        return (List<String>) redisTemplate.opsForList().range(messageThreadId, 0, 20);
    }

    private String concatSenderAndText(Message message) {
        var isChannelAdmin = message.getFrom().getFirstName().equals("Telegram") || message.getFrom().getFirstName().equals("Group");
        return isChannelAdmin ? "Администратор канала: " + message.getText() : message.getFrom().getFirstName() + ": " + message.getText();
    }
}
