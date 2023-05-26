package ru.practicum.shareit.exception;

public class ErrorResponse {
    private final String error;

    public ErrorResponse(String message) {
        this.error = message;
    }
}
