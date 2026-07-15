package com.example.cryptobot.unit;

import com.example.cryptobot.jpa.SubscribeEntity;
import com.example.cryptobot.jpa.SubscribeRepository;
import com.example.cryptobot.jpa.SubscribeService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

class SubscribeServiceTest {
    @Test
    void shouldSaveSubscription() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.existsByChatIdAndCoin("1", "BTC")).thenReturn(false);

        service.subscribe("1", "btc");

        verify(repository).save(argThat(entity ->
                entity.getChatId().equals("1") &&
                        entity.getCoin().equals("BTC")
        ));
    }

    @Test
    void shouldNotSaveExistingSubscription() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.existsByChatIdAndCoin("1", "BTC")).thenReturn(true);

        service.subscribe("1", "BTC");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnTrueWhenSubscriptionRemoved() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.deleteByChatIdAndCoin("1", "BTC")).thenReturn(1L);

        boolean result = service.unsubscribe("1", "btc");

        assertTrue(result);
        verify(repository).deleteByChatIdAndCoin("1", "BTC");
    }

    @Test
    void shouldReturnFalseWhenSubscriptionNotFound() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.deleteByChatIdAndCoin("1", "BTC")).thenReturn(0L);

        boolean result = service.unsubscribe("1", "BTC");

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenSubscribed() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.existsByChatIdAndCoin("1", "BTC")).thenReturn(true);

        assertTrue(service.isSubscribed("1", "btc"));
    }

    @Test
    void shouldReturnFalseWhenNotSubscribed() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.existsByChatIdAndCoin("1", "BTC")).thenReturn(false);

        assertFalse(service.isSubscribed("1", "BTC"));
    }

    @Test
    void shouldReturnAllSubscriptions() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        SubscribeEntity entity = new SubscribeEntity();
        entity.setChatId("1");
        entity.setCoin("BTC");

        when(repository.findAll()).thenReturn(List.of(entity));

        List<SubscribeEntity> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("BTC", result.getFirst().getCoin());

        verify(repository).findAll();
    }

    @Test
    void shouldReturnFalseWhenRepositoryThrowsExceptionInIsSubscribed() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.existsByChatIdAndCoin(anyString(), anyString()))
                .thenThrow(new RuntimeException());

        assertFalse(service.isSubscribed("1", "BTC"));
    }

    @Test
    void shouldReturnFalseWhenRepositoryThrowsExceptionInUnsubscribe() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.deleteByChatIdAndCoin(anyString(), anyString()))
                .thenThrow(new RuntimeException());

        assertFalse(service.unsubscribe("1", "BTC"));
    }

    @Test
    void shouldNotThrowExceptionWhenSaveFails() {
        SubscribeRepository repository = mock(SubscribeRepository.class);
        SubscribeService service = new SubscribeService(repository);

        when(repository.existsByChatIdAndCoin("1", "BTC")).thenReturn(false);

        doThrow(new RuntimeException())
                .when(repository).save(any(SubscribeEntity.class));

        assertDoesNotThrow(() -> service.subscribe("1", "BTC"));
    }
}
