package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;

public final class NightUtils {

    public static final LocalTime NIGHT_WINDOW_END = LocalTime.of(6, 0);

    private NightUtils() {
    }

    public static boolean isNightSession(SleepingSession session) {
        boolean crossesMidnight = !session.getStartSleep().toLocalDate()
                .equals(session.getEndSleep().toLocalDate());
        boolean startsBeforeSix = session.getStartSleep().toLocalTime().isBefore(NIGHT_WINDOW_END);
        return crossesMidnight || startsBeforeSix;
    }

    public static LocalDate nightDateOf(SleepingSession session) {
        boolean crossesMidnight = !session.getStartSleep().toLocalDate()
                .equals(session.getEndSleep().toLocalDate());
        return crossesMidnight ? session.getEndSleep().toLocalDate() : session.getStartSleep().toLocalDate();
    }
}