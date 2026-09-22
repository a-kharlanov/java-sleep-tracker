package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxDurationFunctionTest {

    private final MaxDurationFunction function = new MaxDurationFunction();

    @Test
    void findsMaximumAmongSeveralSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0), SleepQuality.GOOD), // 480 мин
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30), SleepQuality.NORMAL) // 420 мин
        );

        assertEquals(480L, function.apply(sessions).getValue());
    }

    @Test
    void returnsSessionDurationWhenOnlyOneSession() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0), SleepQuality.GOOD) // 480 мин
        );

        assertEquals(480L, function.apply(sessions).getValue());
    }
}