package com.telegrambot.kocherovbot.dto;

import com.telegrambot.kocherovbot.domen.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    private String model;
    private List<Message> messages;
    @Builder.Default
    private int n = 1;
    @Builder.Default
    private double temperature = 1;
}
