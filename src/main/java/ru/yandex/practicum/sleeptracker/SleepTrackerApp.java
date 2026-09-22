package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private final List<Function<List<SleepingSession>, SleepAnalysisResult>> analysisFunctions = List.of(
            new SessionCountFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AverageDurationFunction(),
            new BadQualityCountFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeFunction()
    );

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Укажите путь к файлу с логом сна первым аргументом.");
            return;
        }

        SleepTrackerApp app = new SleepTrackerApp();
        List<SleepingSession> sessions = new SleepLogReader().readSessions(Path.of(args[0]));

        app.analysisFunctions.forEach(function -> {
            SleepAnalysisResult result = function.apply(sessions);
            System.out.println(result.getDescription() + result.getValue());
        });
    }
}