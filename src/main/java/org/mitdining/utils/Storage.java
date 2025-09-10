package org.mitdining.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.mitdining.model.Day;
import org.mitdining.model.DiningHall;
import org.mitdining.model.TimeRange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Storage {
    private static final String DATA_FILE_PATH = System.getenv("DATA_FILE_PATH");
    private List<DiningHall> diningHalls;
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
        .setPrettyPrinting()
        .create();

    public Storage() {

    }

    public void initialize() {
        Path filePath = Path.of(DATA_FILE_PATH);
        try {
            if (!Files.exists(filePath)) {
                throw new IOException("File not found: " + filePath);
            }
            TypeToken<List<DiningHall>> collectionType = new TypeToken<>(){};
            diningHalls = gson.fromJson(Files.readString(filePath), collectionType);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }


    public List<DiningHall> getDiningHalls() {
        return diningHalls;
    }

    public Map<DiningHall, TimeRange> getOpenDiningHalls() {
        Map<DiningHall, TimeRange> openDiningHallsAndTimes = new HashMap<>();
        for (DiningHall diningHall : diningHalls) {
            TimeRange open = diningHall.openTimeRange();
            if (open != null) {
                openDiningHallsAndTimes.put(diningHall, open);
            }
        }
        return openDiningHallsAndTimes;
    }

    public Map<DiningHall, TimeRange> getOpenDiningHalls(Day day, LocalTime time) {
        Map<DiningHall, TimeRange> openDiningHallsAndTimes = new HashMap<>();
        for (DiningHall diningHall : diningHalls) {
            TimeRange open = diningHall.openTimeRange(day, time);
            if (open != null) {
                openDiningHallsAndTimes.put(diningHall, open);
            }
        }
        return openDiningHallsAndTimes;
    }

    public static void saveDiningHalls(List<DiningHall> diningHalls) {
        Path filePath = Path.of(DATA_FILE_PATH);
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.writeString(filePath, gson.toJson(diningHalls));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
