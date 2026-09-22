package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime NIGHT_WINDOW_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.NOON;

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей: ", 0L);
        }

        LocalDate firstNight = determineFirstNight(sessions.get(0));
        LocalDate lastNight = sessions.get(sessions.size() - 1).getEndSleep().toLocalDate();

        Set<LocalDate> coveredNights = sessions.stream()
                .map(this::nightCoveredBy)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight.plusDays(1));
        long sleeplessNights = totalNights - coveredNights.size();

        return new SleepAnalysisResult("Количество бессонных ночей: ", sleeplessNights);
    }

    private LocalDate determineFirstNight(SleepingSession firstSession) {
        LocalDate startDate = firstSession.getStartSleep().toLocalDate();
        LocalTime startTime = firstSession.getStartSleep().toLocalTime();
        return startTime.isAfter(NOON) ? startDate.plusDays(1) : startDate;
    }

    private Optional<LocalDate> nightCoveredBy(SleepingSession session) {
        LocalDate startDate = session.getStartSleep().toLocalDate();
        LocalDate endDate = session.getEndSleep().toLocalDate();
        LocalTime startTime = session.getStartSleep().toLocalTime();

        if (!startDate.equals(endDate)) {
            return Optional.of(endDate);
        }
        if (startTime.isBefore(NIGHT_WINDOW_END)) {
            return Optional.of(startDate);
        }
        return Optional.empty();
    }
}