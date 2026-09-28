package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult<Long>> {

    private static final LocalTime NOON = LocalTime.NOON;

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей: ", 0L);
        }

        LocalDate firstNight = determineFirstNight(sessions.getFirst());
        LocalDate lastNight = sessions.getLast().getEndSleep().toLocalDate();

        Set<LocalDate> coveredNights = sessions.stream()
                .filter(NightUtils::isNightSession)
                .map(NightUtils::nightDateOf)
                .collect(Collectors.toSet());

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight.plusDays(1));
        long sleeplessNights = totalNights - coveredNights.size();

        return new SleepAnalysisResult<>("Количество бессонных ночей: ", sleeplessNights);
    }

    private LocalDate determineFirstNight(SleepingSession firstSession) {
        LocalDate startDate = firstSession.getStartSleep().toLocalDate();
        LocalTime startTime = firstSession.getStartSleep().toLocalTime();
        return startTime.isAfter(NOON) ? startDate.plusDays(1) : startDate;
    }
}