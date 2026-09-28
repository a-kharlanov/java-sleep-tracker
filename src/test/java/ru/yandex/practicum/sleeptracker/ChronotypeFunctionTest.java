package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChronotypeFunctionTest {

    private final ChronotypeFunction function = new ChronotypeFunction();

    @Test
    void classifiesAsOwlWhenMajorityOfNightsAreOwlType() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 45),
                        LocalDateTime.of(2025, 10, 3, 9, 15), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 21, 0),
                        LocalDateTime.of(2025, 10, 4, 6, 0), SleepQuality.GOOD) // жаворонок
        );

        assertEquals(Chronotype.OWL, function.apply(sessions).getValue());
    }

    @Test
    void classifiesAsLarkWhenMajorityOfNightsAreLarkType() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 21, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 30), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 45), SleepQuality.GOOD)
        );

        assertEquals(Chronotype.LARK, function.apply(sessions).getValue());
    }

    @Test
    void classifiesAsDoveOnTie() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.GOOD), // сова
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 21, 0),
                        LocalDateTime.of(2025, 10, 3, 6, 0), SleepQuality.GOOD) // жаворонок
        );

        assertEquals(Chronotype.DOVE, function.apply(sessions).getValue());
    }

    @Test
    void classifiesAsDoveWhenTimesDoNotMatchOwlOrLarkPattern() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.GOOD) // сова по началу, но не по концу
        );

        assertEquals(Chronotype.DOVE, function.apply(sessions).getValue());
    }

    @Test
    void ignoresDaytimeSessionsWhenClassifyingChronotype() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 21, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 30), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 13, 0),
                        LocalDateTime.of(2025, 10, 2, 14, 0), SleepQuality.NORMAL), // дневной сон
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 45), SleepQuality.GOOD)
        );

        assertEquals(Chronotype.LARK, function.apply(sessions).getValue());
    }

    @Test
    void treatsTwoSessionsInSameNightAsOneNightForClassification() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 2, 0), SleepQuality.NORMAL), // проснулся ночью
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 3, 0),
                        LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.NORMAL) // снова заснул, та же ночь
        );

        assertEquals(Chronotype.OWL, function.apply(sessions).getValue());
    }

    @Test
    void returnsDoveForEmptyList() {
        assertEquals(Chronotype.DOVE, function.apply(List.of()).getValue());
    }

    @Test
    void exactlyElevenPmIsNotOwl() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.GOOD)
        );

        assertEquals(Chronotype.DOVE, function.apply(sessions).getValue());
    }

    @Test
    void exactlyTenPmIsNotLark() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 30), SleepQuality.GOOD)
        );

        assertEquals(Chronotype.DOVE, function.apply(sessions).getValue());
    }

    @Test
    void fallingAsleepJustAfterMidnightIsNotLark() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 5, 0, 10),
                        LocalDateTime.of(2025, 10, 5, 6, 20), SleepQuality.GOOD)
        );

        assertEquals(Chronotype.DOVE, function.apply(sessions).getValue());
    }

    @Test
    void lateNightSleepCrossingMidnightIsCorrectlyClassifiedAsOwl() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 5, 0, 30),
                        LocalDateTime.of(2025, 10, 5, 9, 30), SleepQuality.GOOD)
        );

        assertEquals(Chronotype.OWL, function.apply(sessions).getValue());
    }
}