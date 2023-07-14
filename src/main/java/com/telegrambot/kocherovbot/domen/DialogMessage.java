package com.telegrambot.kocherovbot.domen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DialogMessage {
    private Long chatId;
    private String member;
    private String content;

    @Override
    public String toString() {
        return member + ": " + content + ";";
    }
}
