package org.mitdining.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.mitdining.utils.LocalTimeAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiningHall {
    private final Map<Day, List<TimeRange>> openTimes;
    private final String name;

    public DiningHall(Map<Day, List<TimeRange>> openTimes, String name) {
        this.openTimes = openTimes;
        this.name = name;
    }

    public TimeRange openTimeRange(Day day, LocalTime time) {
        for (TimeRange timeRange : openTimes.get(day)) {
                if (timeRange.isOpen(time)) {
                    return timeRange;
                }
        }
        return null;
    }

    public TimeRange openTimeRange(Day day) {
        for (TimeRange timeRange : openTimes.get(day)) {
            if (timeRange.isOpen(LocalTime.now())) {
                return timeRange;
            }
        }
        return null;
    }

    public TimeRange openTimeRange() {
        for (TimeRange timeRange : openTimes.get(Day.valueOf(LocalDate
                .now()
                .getDayOfWeek()
                .toString()
                .substring(0, 3)
                .toUpperCase()))) {
            if (timeRange.isOpen(LocalTime.now())) {
                return timeRange;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        Gson gson  = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .setPrettyPrinting()
                .create();
/*        return "DiningHall{" + "openTimes=" + Arrays.stream(Day
                .values())
                .map((d) -> {
                    return openTimes.get(d).stream().map(TimeRange::toString).collect(Collectors.joining(","));
                })
                .collect(Collectors.joining(",")) + '}';
*/

        return gson.toJson(this);
    }
}
