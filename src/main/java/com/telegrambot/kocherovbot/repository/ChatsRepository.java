package com.telegrambot.kocherovbot.repository;

import java.util.List;

public interface ChatsRepository<S> {
    S saveMessage(S entity);
    S savePost(S entity);
    List<String> findMessagesByThreadId(String messageThreadId);

}
