package com.example.cryptobot.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {
    long deleteByChatIdAndCoin(String chatId, String coin);
    boolean existsByChatIdAndCoin(String chatId, String coin);
}
