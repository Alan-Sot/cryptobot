package com.example.cryptobot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class CoinGeckoClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final WebClient webClient;
    private final ObjectMapper mapper;

    private static final Map<String, String> SYMBOL_TO_ID = Map.ofEntries(
            Map.entry("BTC", "bitcoin"),
            Map.entry("ETH", "ethereum")
    );

    public CoinGeckoClient(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    public String getPrice(String symbol) {
        try {
            String id = SYMBOL_TO_ID.get(symbol.toUpperCase());
            if (id == null) {
                log.error("Неизвестный тикер для CoinGecko: {}", symbol);
                return "error";
            }

            var response = webClient.get()
                    .uri("/api/v3/simple/price?ids=" + id + "&vs_currencies=usd")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            var root = mapper.readTree(response);
            var priceNode = root.get(id);

            if (priceNode == null || priceNode.get("usd") == null) {
                log.error("CoinGecko не вернул данных для символа {}", symbol);
                return "error";
            }

            return priceNode.get("usd").asText();
        } catch (Exception e) {
            log.error("Ошибка при получении данных {} из CoinGecko : ", symbol, e);
            return "error";
        }
    }
}
