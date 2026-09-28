package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeFunction implements Function<List<SleepingSession>, SleepAnalysisResult<Chronotype>> {

    private static final LocalTime OWL_START_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_END_AFTER = LocalTime.of(9, 0);
    private static final LocalTime LARK_START_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_END_BEFORE = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        Map<LocalDate, List<SleepingSession>> sessionsByNight = sessions.stream()
                .filter(NightUtils::isNightSession)
                .collect(Collectors.groupingBy(NightUtils::nightDateOf));

        Map<Chronotype, Long> counts = sessionsByNight.entrySet().stream()
                .map(entry -> classifyNight(entry.getKey(), entry.getValue()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Chronotype chronotype = counts.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .filter(entry -> isUniqueMax(counts, entry.getValue()))
                .map(Map.Entry::getKey)
                .orElse(Chronotype.DOVE);

        return new SleepAnalysisResult<>("Хронотип пользователя: ", chronotype);
    }

    private Chronotype classifyNight(LocalDate nightDate, List<SleepingSession> nightSessions) {
        LocalDateTime earliestStart = nightSessions.stream()
                .map(SleepingSession::getStartSleep)
                .min(LocalDateTime::compareTo)
                .orElseThrow();
        LocalDateTime latestEnd = nightSessions.stream()
                .map(SleepingSession::getEndSleep)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDateTime owlStartThreshold = nightDate.minusDays(1).atTime(OWL_START_AFTER);
        LocalDateTime owlEndThreshold = nightDate.atTime(OWL_END_AFTER);
        LocalDateTime larkStartThreshold = nightDate.minusDays(1).atTime(LARK_START_BEFORE);
        LocalDateTime larkEndThreshold = nightDate.atTime(LARK_END_BEFORE);

        if (earliestStart.isAfter(owlStartThreshold) && latestEnd.isAfter(owlEndThreshold)) {
            return Chronotype.OWL;
        }
        if (earliestStart.isBefore(larkStartThreshold) && latestEnd.isBefore(larkEndThreshold)) {
            return Chronotype.LARK;
        }
        return Chronotype.DOVE;
    }

    private boolean isUniqueMax(Map<Chronotype, Long> counts, long maxValue) {
        return counts.values().stream().filter(value -> value == maxValue).count() == 1;
    }
}