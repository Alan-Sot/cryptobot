package com.example.cryptobot.service;

import com.example.cryptobot.client.BinanceClient;

public class CryptoService {
    private final BinanceClient client;

    public CryptoService(BinanceClient client) {
        this.client = client;
    }

    public String getPrice(String coin) {
        return client.getPrice(coin.toUpperCase() + "USDT");
    }
}
