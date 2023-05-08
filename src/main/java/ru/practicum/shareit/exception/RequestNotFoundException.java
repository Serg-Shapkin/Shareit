package ru.practicum.shareit.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long id) {
        super(String.format("Запрос с id: %s не найден", id));
    }
}
