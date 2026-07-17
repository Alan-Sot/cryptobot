package com.example.cryptobot.service;

import com.example.cryptobot.client.CoinGeckoClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CryptoService {
    private final CoinGeckoClient client;

    public CryptoService(CoinGeckoClient client) {
        this.client = client;
    }

    public String getPrice(String coin) {
        var rawPrice = client.getPrice(coin.toUpperCase());

        try {
            return new BigDecimal(rawPrice)
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString();
        } catch (NumberFormatException e) {
            return rawPrice;
        }
    }
}
