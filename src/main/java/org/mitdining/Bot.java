package org.mitdining;

import org.mitdining.model.Day;
import org.mitdining.model.DiningHall;
import org.mitdining.model.TimeRange;
import org.mitdining.utils.Scrapper;
import org.mitdining.utils.Storage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {
    private static final String TELEGRAM_BOT_TOKEN = System.getenv("TELEGRAM_BOT_TOKEN");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
    private final Storage storage;
    private final Map<Long, String> users = new HashMap<>();

    public Bot() {

        storage = new Storage();
        storage.initialize();
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

        Message message = update.getMessage();

        if (message.getText() == null) {
            handleInvalidText(message.getChatId());
            return;
        }

        String [] parts =  update.getMessage().getText().split("\\s+");
        String command = parts[0];

        switch (command) {
            case "/start":
                handleStart(parts, message.getFrom().getId(), message.getFrom().getUserName(), message.getChatId());
                break;
            case "/eat":
                handleEat(parts, message.getChatId());
                break;
            default:
                handleInvalidText(message.getChatId());
        }
    }

    private void handleStart(String [] parts, long userId, String userName, long chatId) {
        users.computeIfAbsent(userId,
                (k) -> userName);
        sendText(chatId, "Welcome to the MIT dining halls bot\n" + "You can start with typing /eat command to find a place to eat now\n(or specify the Day in three letter format and Time in Hours:Minutes am/pm format)\n" +
                "E. g.\n" +
                "/eat\n" +
                "/eat TUE 9:45 am");
    }

    private void handleEat(String [] parts, Long chatId) {
        if (parts.length == 1) {
            Map<DiningHall, TimeRange> openNow = storage.getOpenDiningHalls();
            String text = openNow.keySet()
                    .stream()
                    .map((hall) -> hall.getName() + " till " + openNow.get(hall).getClose())
                    .collect(Collectors.joining("\n"));
            if (text.isEmpty()) {
                sendText(chatId, "Sorry, no halls open right now :(");
            }
            sendText(chatId, text);
        } else if (parts.length == 2 || parts.length == 3) {
            throw new RuntimeException("Incorrect day format");
        } else if (parts.length == 4) {
            Day day = null;
            LocalTime time = null;
            try {
                if (parts[1].length() == 3) {
                    day = Day.valueOf(parts[1].toUpperCase());
                } else if (parts[1].length() > 3) {
                    day = Day.valueOf(parts[1].substring(0, 3).toUpperCase());
                } else {
                    throw new RuntimeException("Incorrect day format");
                }
                System.out.println(parts[2] + " " + parts[3]);
                time = LocalTime.parse((parts[2] + " " + parts[3]).toUpperCase(), formatter);


            } catch (RuntimeException e) {
                handleInvalidText(chatId, e);
            } catch (Exception e) {
                if(e.getClass() == RuntimeException.class) {
                    handleInvalidText(chatId);
                }
            }

            if(day == null || time == null) {
                return;
            }

            Map<DiningHall, TimeRange> openNow = storage.getOpenDiningHalls(day, time);
            String text = openNow.keySet()
                    .stream()
                    .map((hall) -> hall.getName() + " till " + openNow.get(hall).getClose().format(formatter))
                    .collect(Collectors.joining("\n"));
            sendText(chatId, text);
        }
    }

    private void handleInvalidText(Long chatId) {
        sendText(chatId, "Please follow the suggested command format");
    }

    private void handleInvalidText(Long chatId, RuntimeException e) {
        sendText(chatId, "Please follow the suggested command format\n" + e.getMessage());
    }

    private void sendText(Long who, String what) {
        SendMessage sm = SendMessage
                .builder()
                .chatId(who.toString())
                .text(what)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
