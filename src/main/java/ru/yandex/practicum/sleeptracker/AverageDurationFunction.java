package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class AverageDurationFunction implements Function<List<SleepingSession>, SleepAnalysisResult<Double>> {

    @Override
    public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
        double average = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0);
        return new SleepAnalysisResult<>("Средняя продолжительность сессии сна (мин): ", average);
    }
}