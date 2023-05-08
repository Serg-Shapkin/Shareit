package ru.practicum.shareit.exception;

public class UserCreateException extends RuntimeException {
    public UserCreateException() {
        super("Невозможно создать пользователя");
    }
}
