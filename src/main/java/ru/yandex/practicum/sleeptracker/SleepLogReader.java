package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SleepLogReader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public List<SleepingSession> readSessions(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        return IntStream.range(0, lines.size())
                .filter(i -> !lines.get(i).isBlank())
                .mapToObj(i -> parseLine(lines.get(i), i + 1))
                .collect(Collectors.toList());
    }

    private SleepingSession parseLine(String line, int lineNumber) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Некорректный формат строки " + lineNumber + ": ожидается 3 поля через ';', получено " + parts.length);
        }

        LocalDateTime start = parseDateTime(parts[0].trim(), lineNumber);
        LocalDateTime end = parseDateTime(parts[1].trim(), lineNumber);
        SleepQuality quality = parseQuality(parts[2].trim(), lineNumber);
        return new SleepingSession(start, end, quality);
    }

    private LocalDateTime parseDateTime(String text, int lineNumber) {
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Некорректный формат даты/времени в строке " + lineNumber + ": \"" + text + "\"", e);
        }
    }

    private SleepQuality parseQuality(String text, int lineNumber) {
        try {
            return SleepQuality.valueOf(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Некорректное значение качества сна в строке " + lineNumber + ": \"" + text + "\"", e);
        }
    }
}