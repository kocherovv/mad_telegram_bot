package com.telegrambot.kocherovbot.dto;

import com.telegrambot.kocherovbot.domen.GptMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GptRequest {
    private String model;
    private List<GptMessage> messages;
    @Builder.Default
    private int n = 1;
    @Builder.Default
    private double temperature = 1;
}
