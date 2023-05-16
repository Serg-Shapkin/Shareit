package ru.practicum.shareit.exception.user;

public class UserCreateException extends RuntimeException {
    public UserCreateException() {
        super("Невозможно создать пользователя");
    }
}
