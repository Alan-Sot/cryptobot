package com.example.cryptobot.config;

import com.example.cryptobot.bot.CryptoBot;
import com.example.cryptobot.client.CoinGeckoClient;
import com.example.cryptobot.jpa.SubscribeService;
import com.example.cryptobot.service.CryptoService;
import com.example.cryptobot.service.PriceScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CryptoBot cryptoBot(CryptoService cryptoService,
                               TelegramBotConfig telegramBotConfig,
                               SubscribeService subscribeService) {
        return new CryptoBot(cryptoService, telegramBotConfig, subscribeService);
    }

    @Bean
    public CryptoService cryptoService(CoinGeckoClient client) {
        return new CryptoService(client);
    }

    @Bean
    public TelegramBotConfig telegramBotConfig() {
        return new TelegramBotConfig();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(CryptoBot bot) throws Exception {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }

    @Bean
    public CoinGeckoClient coinGeckoClient(ObjectMapper objectMapper) {
        return new CoinGeckoClient(WebClient.create("https://api.coingecko.com"), objectMapper);
    }

    @Bean
    public PriceScheduler priceScheduler(SubscribeService subscribeService,
                                         CryptoService cryptoService,
                                         CryptoBot bot) {
        return new PriceScheduler(subscribeService, cryptoService, bot);
    }
}
