package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private final List<Function<List<SleepingSession>, ? extends SleepAnalysisResult<?>>> analysisFunctions = List.of(
            new SessionCountFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AverageDurationFunction(),
            new BadQualityCountFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeFunction()
    );

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Укажите путь к файлу с логом сна первым аргументом.");
            return;
        }

        List<SleepingSession> sessions;
        try {
            sessions = new SleepLogReader().readSessions(Path.of(args[0]));
        } catch (NoSuchFileException e) {
            System.out.println("Файл не найден: " + e.getFile());
            return;
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка в файле лога: " + e.getMessage());
            return;
        }

        SleepTrackerApp app = new SleepTrackerApp();
        app.analysisFunctions.forEach(function -> {
            SleepAnalysisResult<?> result = function.apply(sessions);
            System.out.println(result.getDescription() + formatValue(result.getValue()));
        });
    }

    private static String formatValue(Object value) {
        if (value instanceof Double d) {
            return String.format("%.2f", d);
        }
        return String.valueOf(value);
    }
}