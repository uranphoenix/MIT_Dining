package org.mitdining.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mitdining.model.Day;
import org.mitdining.model.DiningHall;

import org.jsoup.nodes.Document;
import org.mitdining.model.TimeRange;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Scrapper {
    private static final String MIT_DINING_URL = System.getenv("MIT_DINING_URL");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public static DiningHall loadDiningHallTimes(String hallName) throws IOException {
        Document doc = Jsoup.connect(MIT_DINING_URL + "/" + hallName).get();
        List<String> data = doc
                .getElementsByClass("day-part dotted-leader-container")
                .stream()
                .map((el) -> el.getElementsByClass("dotted-leader-content pull-right")
                        .first()
                        .text()
                        .toUpperCase())
                .toList();

        return new DiningHall(parse(data, hallName), hallName);
    }

    private static Map<Day, List<TimeRange>> parse(List<String> data, String hallName) {
        Map<Day, List<TimeRange>> schedule = new EnumMap<>(Day.class);
        for (String line : data) {
            String [] parts = line.split(",");

            List<Day> days = parseDays(parts[0].trim(), hallName);
            parseAndAddTimeRange(parts[1].trim(), days, schedule, hallName);
        }
        return schedule;
    }

    private static List<Day> parseDays(String daysString, String hallName) {
        List<Day> days;
        if (daysString.contains("/")) {
            String [] parts = daysString.split("/");
            days = parseDayRange(parts[0].trim());
            days.add(parseOneDay(parts[1].trim()));

            return days;
        } else {
            return parseDayRange(daysString);
        }
    }

    private static List<Day> parseDayRange(String dayRangeString) {
        String [] daysRange = dayRangeString.split("-");
        try {
            Day first = Day.valueOf(daysRange[0]);
            Day last = Day.valueOf(daysRange[1]);
            return new ArrayList<>(List.of(Day.values()).subList(first.ordinal(), last.ordinal() + 1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day range string: " + dayRangeString);
        }
    }

    private static Day parseOneDay(String oneDayString) {
        try {
            return Day.valueOf(oneDayString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid one day string: " + oneDayString);
        }
    }

    private static void parseAndAddTimeRange(String timesString, List<Day> days, Map<Day, List<TimeRange>> schedule, String hallName) {
        String [] times = timesString.split(" - ");
        LocalTime open = LocalTime.parse(times[0].trim(), FORMATTER);
        LocalTime close = LocalTime.parse(times[1].trim(), FORMATTER);

        for (Day day : days) {
            if (open.isBefore(close)) {
                TimeRange timeRange = new TimeRange(open, close);
                schedule.computeIfAbsent(day, key -> new ArrayList<>()).add(timeRange);
            } else {
                if (hallName.equals("the-howard-dining-hall-at-maseeh")) {
                    open = open.plusHours(12);
                }
                TimeRange timeRangeToday = new TimeRange(open, LocalTime.MAX);
                TimeRange timeRangeTomorrow = new TimeRange(LocalTime.MIN, close);
                Day tomorrowDay = Day.values()[(day.ordinal() + 1) % Day.values().length];

                schedule.computeIfAbsent(day, key -> new ArrayList<>()).add(timeRangeToday);
                schedule.computeIfAbsent(tomorrowDay, key -> new ArrayList<>()).add(timeRangeTomorrow);
            }
        }
    }
}
