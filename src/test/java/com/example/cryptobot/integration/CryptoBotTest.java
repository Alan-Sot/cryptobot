package com.example.cryptobot.integration;

import com.example.cryptobot.bot.CryptoBot;
import com.example.cryptobot.config.TelegramBotConfig;
import com.example.cryptobot.integration.config.CryptoBotTestConfiguration;
import com.example.cryptobot.jpa.SubscribeService;
import com.example.cryptobot.service.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="+
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"+
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,",
        "spring.task.scheduling.enabled=false",
        "spring.main.web-application-type=none"
})
@Import(CryptoBotTestConfiguration.class)
public class CryptoBotTest {
    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private TelegramBotConfig config;
    @Autowired
    private SubscribeService subscribeService;
    private TestCryptoBot cryptoBot;
    private List<SendMessage> messages;

    @BeforeEach
    public void init() {
        cryptoBot = new TestCryptoBot(cryptoService, config, subscribeService);
        messages = new ArrayList<>();
    }

    private Update update(String text, long chatId) {
        Update update = new Update();

        Message message = new Message();
        message.setText(text);

        Chat chat = new Chat();
        chat.setId(chatId);

        message.setChat(chat);
        update.setMessage(message);

        return update;
    }

    @Test
    void startCommandTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("/start", 1L));

        assertNotNull(messages);
        assertTrue(messages.getFirst().getText().contains("Меню активировано, пожалуйста, используйте кнопки на панели 🔥"));
        assertNotNull(messages.getFirst().getReplyMarkup());
    }

    @Test
    void btcPriceReqTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("📊 BTC", 1L));

        var response = messages.getFirst().getText();

        assertEquals("BTC: 60133.99 USDT", response);
    }

    @Test
    void ethPriceReqTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("📊 ETH", 1L));

        var response = messages.getFirst().getText();

        assertEquals("ETH: 1561.76 USDT", response);
    }

    @Test
    void btcSubscribeTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("🔔 Подписаться BTC", 1L));

        assertTrue(messages.getFirst().getText().contains("BTC подписка включена"));
    }

    @Test
    void btcUnsubscribeTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("❌ Отписаться BTC", 1L));

        assertTrue(messages.getFirst().getText().contains("BTC подписка отключена"));
    }

    @Test
    void ethSubscribeTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("🔔 Подписаться ETH", 1L));

        assertTrue(messages.getFirst().getText().contains("ETH подписка включена"));
    }

    @Test
    void ethUnsubscribeTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("❌ Отписаться ETH", 1L));

        assertTrue(messages.getFirst().getText().contains("ETH подписка отключена"));
    }

    @Test
    void helpReqTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("ℹ️ HELP", 1L));

        assertTrue(messages.getFirst().getText().contains("Бот крипты"));
    }

    @Test
    void restartReqTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("🔄 RESTART", 1L));

        var response = messages.getFirst();

        assertEquals("Бот перезапущен 🔄", response.getText());
        assertNotNull(response.getReplyMarkup());
    }

    @Test
    void unknownTextReqTest() {
        messages.clear();

        cryptoBot.onUpdateReceived(update("random", 1L));

        assertEquals("Пожалуйста, используйте кнопки!", messages.getFirst().getText());
    }

    private class TestCryptoBot extends CryptoBot {

        public TestCryptoBot(CryptoService cryptoService,
                             TelegramBotConfig config,
                             SubscribeService subscribeService) {
            super(cryptoService, config, subscribeService);
        }

        @Override
        public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
            if (method instanceof SendMessage sm) {
                messages.add(sm);
            }
            return null;
        }
    }
}