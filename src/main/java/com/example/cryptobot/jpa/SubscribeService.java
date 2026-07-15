package com.example.cryptobot.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscribeService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final SubscribeRepository repository;

    public SubscribeService(SubscribeRepository repository) {
        this.repository = repository;
    }

    public void subscribe(String chatId, String coin) {
        coin = normalize(coin);

        if (repository.existsByChatIdAndCoin(chatId, coin)) return;

        var entity = new SubscribeEntity();
        entity.setChatId(chatId);
        entity.setCoin(coin);

        try {
            repository.save(entity);
        } catch (Exception e) {
            log.error("Ошибка при оформлении подписки {}. chatId : {}", coin, chatId, e);
        }
    }

    @Transactional
    public boolean unsubscribe(String chatId, String coin) {
        coin = normalize(coin);

        try {
            return repository.deleteByChatIdAndCoin(chatId, coin) > 0;
        } catch (Exception e) {
            log.error("Ошибка при отмене подписки {}. chatId : {}", coin, chatId, e);
            return false;
        }
    }

    public boolean isSubscribed(String chatId, String coin) {
        try {
            coin = normalize(coin);
            return repository.existsByChatIdAndCoin(chatId, coin);
        } catch (Exception e) {
            log.error("Ошибка получения информации по подписке. chatId : {}, coin = {}", chatId, coin);
            return false;
        }
    }

    public List<SubscribeEntity> getAll() {
        return repository.findAll();
    }

    private String normalize(String coin) {
        if (coin == null) return null;
        return coin.trim().toUpperCase();
    }
}
