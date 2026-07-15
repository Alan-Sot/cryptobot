package com.example.cryptobot.service;

import com.example.cryptobot.bot.CryptoBot;
import com.example.cryptobot.jpa.SubscribeEntity;
import com.example.cryptobot.jpa.SubscribeService;
import org.springframework.scheduling.annotation.Scheduled;

public class PriceScheduler {
    private final SubscribeService subscribeService;
    private final CryptoService cryptoService;
    private final CryptoBot bot;

    public PriceScheduler(SubscribeService subscribeService,
                          CryptoService cryptoService,
                          CryptoBot bot) {
        this.subscribeService = subscribeService;
        this.cryptoService = cryptoService;
        this.bot = bot;
    }

    @Scheduled(fixedRate = 60000)
    public void sendPrices() {
        for (SubscribeEntity sub : subscribeService.getAll()) {

            String price = cryptoService.getPrice(sub.getCoin());

            bot.sendMessage(
                    sub.getChatId(),
                    "⏱ " + sub.getCoin() + ": " + price + " USDT"
            );
        }
    }
}