package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;

public class ConditionsNotMetException extends RuntimeException {
    public ConditionsNotMetException(String message, Logger log) {
        super(message);
        log.error(message, this);
    }
}