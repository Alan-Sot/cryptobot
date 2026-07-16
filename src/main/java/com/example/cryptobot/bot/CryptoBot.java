package com.example.cryptobot.bot;

import com.example.cryptobot.config.TelegramBotConfig;
import com.example.cryptobot.jpa.SubscribeService;
import com.example.cryptobot.service.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class CryptoBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CryptoService cryptoService;
    private final TelegramBotConfig config;
    private final SubscribeService subscribeService;

    public CryptoBot(CryptoService cryptoService,
                     TelegramBotConfig config,
                     SubscribeService subscribeService) {
        this.cryptoService = cryptoService;
        this.config = config;
        this.subscribeService = subscribeService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        var chatId = update.getMessage().getChatId().toString();
        var text = update.getMessage().getText().trim();

        if (text.equals("/start")) {
            sendWithKeyboard(chatId, "Меню активировано, пожалуйста, используйте кнопки на панели 🔥", createKeyboard(chatId));
            return;
        }

        switch (text) {
            case "📊 BTC" -> send(chatId, "BTC: " + cryptoService.getPrice("BTC") + " USDT");
            case "📊 ETH" -> send(chatId, "ETH: " + cryptoService.getPrice("ETH") + " USDT");
            case "🔔 Подписаться BTC" -> {
                subscribeService.subscribe(chatId, "BTC");
                sendWithKeyboard(chatId, "BTC подписка включена ✅", createKeyboard(chatId));
                send(chatId, "⏱ BTC: " + cryptoService.getPrice("BTC") + " USDT");
            }
            case "❌ Отписаться BTC" -> {
                subscribeService.unsubscribe(chatId, "BTC");
                sendWithKeyboard(chatId, "BTC подписка отключена ❌", createKeyboard(chatId));
            }
            case "🔔 Подписаться ETH" -> {
                subscribeService.subscribe(chatId, "ETH");
                sendWithKeyboard(chatId, "ETH подписка включена ✅", createKeyboard(chatId));
                send(chatId, "⏱ ETH: " + cryptoService.getPrice("ETH") + " USDT");
            }
            case "❌ Отписаться ETH" -> {
                subscribeService.unsubscribe(chatId, "ETH");
                sendWithKeyboard(chatId, "ETH подписка отключена ❌", createKeyboard(chatId));
            }
            case "ℹ️ HELP" -> send(chatId,
                    "📌 Бот крипты:\n" +
                            "📊 BTC / ETH — цена\n" +
                            "🔔 Подписки через кнопки\n" +
                            "🔄 RESTART — обновить меню"
            );
            case "🔄 RESTART" -> sendWithKeyboard(chatId, "Бот перезапущен 🔄", createKeyboard(chatId));
            default -> sendWithKeyboard(chatId, "Пожалуйста, используйте кнопки!", createKeyboard(chatId));
        }
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private ReplyKeyboardMarkup createKeyboard(String chatId) {
        var row1 = new KeyboardRow();
        var row2 = new KeyboardRow();
        var row3 = new KeyboardRow();

        row1.add(subscribeService.isSubscribed(chatId, "BTC") ? "❌ Отписаться BTC" : "🔔 Подписаться BTC");
        row1.add(subscribeService.isSubscribed(chatId, "ETH") ? "❌ Отписаться ETH" : "🔔 Подписаться ETH");

        row2.add("📊 BTC");
        row2.add("📊 ETH");

        row3.add("ℹ️ HELP");
        row3.add("🔄 RESTART");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        var kb = new ReplyKeyboardMarkup();
        kb.setKeyboard(rows);
        kb.setResizeKeyboard(true);
        kb.setOneTimeKeyboard(false);

        return kb;
    }

    private void send(String chatId, String text) {
        try {
            execute(new SendMessage(chatId, text));
        } catch (Exception e) {
            log.error("Ошибка отправки сообщекния. chatId : {}", chatId, e);
        }
    }

    private void sendWithKeyboard(String chatId, String text, ReplyKeyboardMarkup keyboard) {
        try {
            var msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText(text);

            if (keyboard != null) {
                msg.setReplyMarkup(keyboard);
            }

            execute(msg);
        } catch (Exception e) {
            log.error("Ошибка формирования панели с кнопками. chatId : {}", chatId, e);
        }
    }

    public void sendMessage(String chatId, String text) {
        send(chatId, text);
    }
}
