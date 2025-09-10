package org.mitdining.model;

import java.time.LocalTime;
import java.util.Date;

public class TimeRange {
    private final LocalTime open;
    private final LocalTime close;

    public TimeRange(LocalTime open, LocalTime close) {
        this.open = open;
        this.close = close;
    }

    boolean isOpen(LocalTime time) {
        return time.isAfter(open) && time.isBefore(close);
    }

    public LocalTime getOpen() {
        return open;
    }

    public LocalTime getClose() {
        return close;
    }

    @Override
    public String toString() {
        return "TimeRange{" + "open=" + open + ", close=" + close + '}';
    }
}
