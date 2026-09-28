package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleepLogReaderTest {

    private final SleepLogReader reader = new SleepLogReader();

    @Test
    void parsesValidLogFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("log.txt");
        Files.writeString(file, "01.10.25 23:15;02.10.25 07:30;GOOD");

        List<SleepingSession> sessions = reader.readSessions(file);

        assertEquals(1, sessions.size());
        assertEquals(SleepQuality.GOOD, sessions.getFirst().getQuality());
    }

    @Test
    void throwsOnMissingField(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("log.txt");
        Files.writeString(file, "01.10.25 23:15;02.10.25 07:30");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readSessions(file));
        assertTrue(exception.getMessage().contains("1"));
    }

    @Test
    void throwsOnInvalidDateFormat(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("log.txt");
        Files.writeString(file, "2025-10-01 23:15;02.10.25 07:30;GOOD");

        assertThrows(IllegalArgumentException.class, () -> reader.readSessions(file));
    }

    @Test
    void throwsOnInvalidQuality(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("log.txt");
        Files.writeString(file, "01.10.25 23:15;02.10.25 07:30;good");

        assertThrows(IllegalArgumentException.class, () -> reader.readSessions(file));
    }

    @Test
    void reportsCorrectLineNumberForSecondLine(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("log.txt");
        Files.writeString(file,
                "01.10.25 23:15;02.10.25 07:30;GOOD" + System.lineSeparator() + "bad line");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readSessions(file));
        assertTrue(exception.getMessage().contains("2"));
    }
}