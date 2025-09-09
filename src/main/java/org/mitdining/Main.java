package org.mitdining;

import org.mitdining.model.DiningHall;
import org.mitdining.utils.Scrapper;
import org.mitdining.utils.Storage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*
        Scrapper.loadTimes("");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            System.out.println("Unable to create a bot");
        }
        */
        //Storage storage = new Storage();
        //storage.initialize();
        try {
            DiningHall nv = Scrapper.loadDiningHallTimes("new-vassar");
            System.out.println(nv);
            Storage.saveDiningHalls(List.of(nv));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}