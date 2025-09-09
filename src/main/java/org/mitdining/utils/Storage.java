package org.mitdining.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
//    private static final List<DiningHall> diningHalls = new ArrayList<>();
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
        .setPrettyPrinting()
        .create();

    public void initialize() {
        Path filePath = Path.of(DATA_FILE_PATH);
//        TimeRange timeRange = new TimeRange(8.0f, 10.0f);
//        TimeRange timeRange2 = new TimeRange(7.0f, 10.0f);
        Map<Day, List<TimeRange>> schedule = new HashMap<>();
 //       schedule.put(Day.TUE, List.of(timeRange));
        Map<Day, List<TimeRange>> schedule2 = new HashMap<>();
 //       schedule2.put(Day.TUE, List.of(timeRange2));
 //       diningHalls.add(new DiningHall(schedule));
  //      diningHalls.add(new DiningHall(schedule2));

        try {
            if (!Files.exists(filePath)) {
                throw new IOException("File not found: " + filePath);
            }
            TypeToken<List<DiningHall>> collectionType = new TypeToken<>(){};
            List<DiningHall> din = gson.fromJson(Files.readString(filePath), collectionType);
            for (DiningHall d : din) {
                System.out.println(d);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            //System.err.println(e.getMessage());
        }


    }


    public static List<DiningHall> getDiningHalls() {
        return null;
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
