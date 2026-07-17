package com.example.cryptobot.unit;

import com.example.cryptobot.client.CoinGeckoClient;
import com.example.cryptobot.service.CryptoService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CryptoServiceTest {
    @Test
    void shouldConvertToUsdt() {
        CoinGeckoClient client = mock(CoinGeckoClient.class);
        when(client.getPrice("BTC")).thenReturn("60133.99");
        when(client.getPrice("ETH")).thenReturn("1561.76");

        CryptoService service = new CryptoService(client);

        assertEquals("60133.99", service.getPrice("BTC"));
        assertEquals("1561.76", service.getPrice("ETH"));
    }
}
