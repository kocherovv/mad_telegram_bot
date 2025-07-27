package com.telegrambot.kocherovbot;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@SpringBootApplication
@Log4j2
public class KocherovBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(KocherovBotApplication.class, args);
    }
}
