package org.mitdining;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");

    public Bot(String filePath) {

    }


    @Override
    public String getBotUsername() {
        return "mit_dining_bot";
    }

    @Override
    public String getBotToken() {
        return TELEGRAM_BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
    }
}
