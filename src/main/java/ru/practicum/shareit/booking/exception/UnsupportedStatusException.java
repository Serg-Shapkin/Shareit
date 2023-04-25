package ru.practicum.shareit.booking.exception;

public class UnsupportedStatusException extends IllegalArgumentException {
    public UnsupportedStatusException(String state) {
        super(String.format("Передан некорректный параметр: %s", state));
    }
}
