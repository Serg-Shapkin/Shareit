package ru.practicum.shareit.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super(String.format("Пользователь с id: %s не найден", id));
    }
}
