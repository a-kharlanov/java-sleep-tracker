package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime NIGHT_WINDOW_END = LocalTime.of(6, 0);
    private static final LocalTime OWL_START_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_END_AFTER = LocalTime.of(9, 0);
    private static final LocalTime LARK_START_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_END_BEFORE = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        Map<LocalDate, List<SleepingSession>> sessionsByNight = sessions.stream()
                .filter(this::isNightSession)
                .collect(Collectors.groupingBy(this::nightDateOf));

        Map<Chronotype, Long> counts = sessionsByNight.values().stream()
                .map(this::classifyNight)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Chronotype chronotype = counts.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .filter(entry -> isUniqueMax(counts, entry.getValue()))
                .map(Map.Entry::getKey)
                .orElse(Chronotype.DOVE);

        return new SleepAnalysisResult("Хронотип пользователя: ", chronotype);
    }

    private boolean isNightSession(SleepingSession session) {
        boolean crossesMidnight = !session.getStartSleep().toLocalDate()
                .equals(session.getEndSleep().toLocalDate());
        boolean startsBeforeSix = session.getStartSleep().toLocalTime().isBefore(NIGHT_WINDOW_END);
        return crossesMidnight || startsBeforeSix;
    }

    private LocalDate nightDateOf(SleepingSession session) {
        boolean crossesMidnight = !session.getStartSleep().toLocalDate()
                .equals(session.getEndSleep().toLocalDate());
        return crossesMidnight ? session.getEndSleep().toLocalDate() : session.getStartSleep().toLocalDate();
    }

    private Chronotype classifyNight(List<SleepingSession> nightSessions) {
        LocalDateTime earliestStart = nightSessions.stream()
                .map(SleepingSession::getStartSleep)
                .min(LocalDateTime::compareTo)
                .orElseThrow();
        LocalDateTime latestEnd = nightSessions.stream()
                .map(SleepingSession::getEndSleep)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        LocalTime start = earliestStart.toLocalTime();
        LocalTime end = latestEnd.toLocalTime();

        if (start.isAfter(OWL_START_AFTER) && end.isAfter(OWL_END_AFTER)) {
            return Chronotype.OWL;
        }
        if (start.isBefore(LARK_START_BEFORE) && end.isBefore(LARK_END_BEFORE)) {
            return Chronotype.LARK;
        }
        return Chronotype.DOVE;
    }

    private boolean isUniqueMax(Map<Chronotype, Long> counts, long maxValue) {
        return counts.values().stream().filter(value -> value == maxValue).count() == 1;
    }
}