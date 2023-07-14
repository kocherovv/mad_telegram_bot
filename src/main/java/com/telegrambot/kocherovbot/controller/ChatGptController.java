package com.telegrambot.kocherovbot.controller;

import com.telegrambot.kocherovbot.domen.GptMessage;
import com.telegrambot.kocherovbot.dto.GptRequest;
import com.telegrambot.kocherovbot.dto.GptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@RestController
public class ChatGptController {
    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        var messages = new ArrayList<GptMessage>();
        messages.add(GptMessage.builder()
            .role("user")
            .content(prompt)
            .build());

        var request = GptRequest.builder()
            .model(model)
            .messages(messages)
            .build();
        var response = restTemplate.postForObject(apiUrl, request, GptResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}
