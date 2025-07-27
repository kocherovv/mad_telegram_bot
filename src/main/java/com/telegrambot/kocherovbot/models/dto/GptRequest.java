package com.telegrambot.kocherovbot.models.dto;

import com.telegrambot.kocherovbot.models.GptMessage;
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
