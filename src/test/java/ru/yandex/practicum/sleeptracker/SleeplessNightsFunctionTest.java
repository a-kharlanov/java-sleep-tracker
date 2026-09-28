package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SleeplessNightsFunctionTest {

    private final SleeplessNightsFunction function = new SleeplessNightsFunction();

    @Test
    void noSleeplessNightsWhenEveryNightCovered() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 6, 0), SleepQuality.GOOD)
        );

        assertEquals(0L, function.apply(sessions).getValue());
    }

    @Test
    void findsSleeplessNightInTheMiddle() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0), SleepQuality.GOOD), // ночь Oct2 закрыта
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 22, 0),
                        LocalDateTime.of(2025, 10, 4, 6, 0), SleepQuality.GOOD) // ночь Oct4 закрыта, Oct3 — нет
        );

        assertEquals(1L, function.apply(sessions).getValue());
    }

    @Test
    void ignoresDaytimeSessionsWhenLookingForSleeplessNights() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 13, 0),
                        LocalDateTime.of(2025, 10, 2, 14, 0), SleepQuality.NORMAL), // дневной сон, не считается
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 22, 0),
                        LocalDateTime.of(2025, 10, 4, 6, 0), SleepQuality.GOOD)
        );

        assertEquals(1L, function.apply(sessions).getValue()); // ночь Oct3 всё равно бессонная
    }

    @Test
    void doesNotDoubleCountTwoSessionsInSameNight() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 1, 0),
                        LocalDateTime.of(2025, 10, 2, 2, 0), SleepQuality.NORMAL),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 3, 0),
                        LocalDateTime.of(2025, 10, 2, 4, 0), SleepQuality.NORMAL)
        );

        assertEquals(0L, function.apply(sessions).getValue());
    }

    @Test
    void handlesExactNoonBoundaryAndAllSleeplessCase() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 12, 0),
                        LocalDateTime.of(2025, 10, 1, 18, 0), SleepQuality.NORMAL) // дневной сон, ровно в полдень
        );

        assertEquals(1L, function.apply(sessions).getValue());
    }

    @Test
    void countsCorrectlyAcrossMonthBoundary() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 1, 30, 2, 0),
                        LocalDateTime.of(2025, 1, 30, 5, 0), SleepQuality.NORMAL), // ночь Jan30 закрыта
                new SleepingSession(LocalDateTime.of(2025, 3, 2, 23, 0),
                        LocalDateTime.of(2025, 3, 3, 6, 0), SleepQuality.GOOD) // ночь Mar3 закрыта
        );

        assertEquals(31L, function.apply(sessions).getValue());
    }

    @Test
    void returnsZeroForEmptyList() {
        assertEquals(0L, function.apply(List.of()).getValue());
    }
}