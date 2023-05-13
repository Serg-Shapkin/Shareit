package ru.practicum.shareit.exception.request;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long id) {
        super(String.format("Запрос с id: %s не найден", id));
    }
}
