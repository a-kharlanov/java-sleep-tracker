package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionCountFunctionTest {

    private final SessionCountFunction function = new SessionCountFunction();

    @Test
    void countsMultipleSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0), SleepQuality.NORMAL)
        );

        assertEquals(2, function.apply(sessions).getValue());
    }

    @Test
    void returnsZeroForEmptyList() {
        assertEquals(0, function.apply(List.of()).getValue());
    }
}