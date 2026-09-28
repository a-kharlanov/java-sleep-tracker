package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class SessionCountFunction implements Function<List<SleepingSession>, SleepAnalysisResult<Integer>> {

    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult<>("Общее количество сессий сна: ", sessions.size());
    }
}