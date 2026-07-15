package com.example.cryptobot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

public class BinanceClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final WebClient webClient;
    private final ObjectMapper mapper;

    public BinanceClient(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    public String getPrice(String symbol) {
        try {
            var response = webClient.get()
                    .uri("/api/v3/ticker/price?symbol=" + symbol)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return mapper.readTree(response).get("price").asText();
        } catch (Exception e) {
            log.error("Ошибка при получении данных {} из Binance : ", symbol, e);
            return "error";
        }
    }
}
