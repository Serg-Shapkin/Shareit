package ru.practicum.shareit.exception;

public class BookingCreateException extends RuntimeException {
    public BookingCreateException(String message) {
        super(message);
    }
}
