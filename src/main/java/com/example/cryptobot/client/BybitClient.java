package com.example.cryptobot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

public class BybitClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final WebClient webClient;
    private final ObjectMapper mapper;

    public BybitClient(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    public String getPrice(String symbol) {
        try {
            var response = webClient.get()
                    .uri("/v5/market/tickers?category=spot&symbol=" + symbol)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            var root = mapper.readTree(response);

            int retCode = root.get("retCode").asInt();
            if (retCode != 0) {
                log.error("Bybit API вернул ошибку для {}: retCode={}, retMsg={}",
                        symbol, retCode, root.get("retMsg").asText());
                return "error";
            }

            var list = root.get("result").get("list");
            if (list == null || list.isEmpty()) {
                log.error("Bybit не вернул данных для символа {}", symbol);
                return "error";
            }

            return list.get(0).get("lastPrice").asText();
        } catch (Exception e) {
            log.error("Ошибка при получении данных {} из Bybit : ", symbol, e);
            return "error";
        }
    }
}
