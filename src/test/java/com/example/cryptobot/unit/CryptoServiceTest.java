package com.example.cryptobot.unit;

import com.example.cryptobot.client.BybitClient;
import com.example.cryptobot.service.CryptoService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CryptoServiceTest {
    @Test
    void shouldConvertToUsdt() {
        BybitClient client = mock(BybitClient.class);
        when(client.getPrice("BTCUSDT")).thenReturn("60133.99");
        when(client.getPrice("ETHUSDT")).thenReturn("1561.76");

        CryptoService service = new CryptoService(client);

        assertEquals("60133.99", service.getPrice("BTC"));
        assertEquals("1561.76", service.getPrice("ETH"));
    }
}
