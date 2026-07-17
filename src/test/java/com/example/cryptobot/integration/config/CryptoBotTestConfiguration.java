package com.example.cryptobot.integration.config;

import com.example.cryptobot.client.CoinGeckoClient;
import com.example.cryptobot.config.TelegramBotConfig;
import com.example.cryptobot.jpa.SubscribeRepository;
import com.example.cryptobot.jpa.SubscribeService;
import com.example.cryptobot.service.CryptoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class CryptoBotTestConfiguration {
    @MockBean
    public TelegramBotsApi telegramBotsApi;

    @Bean
    @Primary
    public CoinGeckoClient coinGeckoTestClient() {
        var client = mock(CoinGeckoClient.class);

        when(client.getPrice("BTC")).thenReturn("60133.99");
        when(client.getPrice("ETH")).thenReturn("1561.76");

        return client;
    }

    @Bean
    @Primary
    public CryptoService cryptoServiceTest(@Qualifier("coinGeckoTestClient") CoinGeckoClient coinGeckoTestClient) {
        return new CryptoService(coinGeckoTestClient);
    }

    @Bean
    @Primary
    public TelegramBotConfig telegramBotTestConfig() {
        return new TelegramBotConfig();
    }

    @Bean
    @Primary
    public SubscribeRepository subscribeTestRepository() {
        var repo = mock(SubscribeRepository.class);

        when(repo.deleteByChatIdAndCoin(any(), any())).thenReturn(0L);
        when(repo.existsByChatIdAndCoin(any(), any())).thenReturn(true);

        return repo;
    }

    @Bean
    @Primary
    public SubscribeService subscribeTestService(@Qualifier("subscribeTestRepository") SubscribeRepository subscribeTestRepository) {
        return new SubscribeService(subscribeTestRepository);
    }
}
