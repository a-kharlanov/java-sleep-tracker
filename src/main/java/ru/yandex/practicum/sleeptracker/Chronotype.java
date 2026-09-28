package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    OWL {
        @Override
        public String toString() {
            return "Сова";
        }
    },
    LARK {
        @Override
        public String toString() {
            return "Жаворонок";
        }
    },
    DOVE {
        @Override
        public String toString() {
            return "Голубь";
        }
    }
}