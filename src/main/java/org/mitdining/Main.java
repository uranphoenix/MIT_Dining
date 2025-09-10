package org.mitdining;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mitdining.model.DiningHall;
import org.mitdining.utils.LocalTimeAdapter;
import org.mitdining.utils.Scrapper;
import org.mitdining.utils.Storage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Bot());
    }

    public static void loadDataToDatabase() throws IOException {
        Storage storage = new Storage();
        storage.initialize();

        List<DiningHall> diningHalls = new ArrayList<>();
        String [] hallNames = storage.getDiningHalls().stream().map(DiningHall::getName).toArray(String[]::new);
        for (String hallName : hallNames) {
            diningHalls.add(Scrapper.loadDiningHallTimes(hallName));
        }
        Storage.saveDiningHalls(diningHalls);
    }
}