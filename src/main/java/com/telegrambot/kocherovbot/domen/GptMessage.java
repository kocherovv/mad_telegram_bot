package com.telegrambot.kocherovbot.domen;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;

    @Override
    public String toString() {
        return role + ": " + content + ";";
    }
}
