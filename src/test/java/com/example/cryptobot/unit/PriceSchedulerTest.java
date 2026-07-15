package com.example.cryptobot.unit;

import com.example.cryptobot.bot.CryptoBot;
import com.example.cryptobot.jpa.SubscribeEntity;
import com.example.cryptobot.jpa.SubscribeService;
import com.example.cryptobot.service.CryptoService;
import com.example.cryptobot.service.PriceScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;

class PriceSchedulerTest {
    @Test
    void shouldSendPriceToAllSubscribers() {
        SubscribeService subscribeService = mock(SubscribeService.class);
        CryptoService cryptoService = mock(CryptoService.class);
        CryptoBot bot = mock(CryptoBot.class);

        PriceScheduler priceScheduler = new PriceScheduler(subscribeService, cryptoService, bot);

        SubscribeEntity btc = new SubscribeEntity();
        btc.setChatId("1");
        btc.setCoin("BTC");

        SubscribeEntity eth = new SubscribeEntity();
        eth.setChatId("2");
        eth.setCoin("ETH");

        when(subscribeService.getAll()).thenReturn(List.of(btc, eth));
        when(cryptoService.getPrice("BTC")).thenReturn("60133.99000000");
        when(cryptoService.getPrice("ETH")).thenReturn("1561.76000000");

        priceScheduler.sendPrices();

        verify(subscribeService).getAll();

        verify(cryptoService).getPrice("BTC");
        verify(cryptoService).getPrice("ETH");

        verify(bot).sendMessage("1", "⏱ BTC: 60133.99000000 USDT");
        verify(bot).sendMessage("2", "⏱ ETH: 1561.76000000 USDT");

        verifyNoMoreInteractions(bot);
    }

    @Test
    void shouldNotSendMessagesWhenThereAreNoSubscribers() {
        SubscribeService subscribeService = mock(SubscribeService.class);
        CryptoService cryptoService = mock(CryptoService.class);
        CryptoBot bot = mock(CryptoBot.class);

        PriceScheduler priceScheduler = new PriceScheduler(subscribeService, cryptoService, bot);

        when(subscribeService.getAll()).thenReturn(List.of());

        priceScheduler.sendPrices();

        verify(subscribeService).getAll();
        verifyNoInteractions(cryptoService);
        verifyNoInteractions(bot);
    }
}
