package com.example.cryptobot.service;

import com.example.cryptobot.client.BybitClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CryptoService {
    private final BybitClient client;

    public CryptoService(BybitClient client) {
        this.client = client;
    }

    public String getPrice(String coin) {
        var rawPrice = client.getPrice(coin.toUpperCase() + "USDT");

        try {
            return new BigDecimal(rawPrice)
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString();
        } catch (NumberFormatException e) {
            return rawPrice;
        }
    }
}
