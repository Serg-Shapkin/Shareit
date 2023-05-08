package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends IllegalArgumentException {
    public UnsupportedStatusException(String state) {
        super(String.format("Передан некорректный параметр: %s", state));
    }
}
