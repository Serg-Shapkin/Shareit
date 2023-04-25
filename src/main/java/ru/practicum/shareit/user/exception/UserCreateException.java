package ru.practicum.shareit.user.exception;

public class UserCreateException extends RuntimeException {
    public UserCreateException() {
        super("Невозможно создать пользователя");
    }
}
