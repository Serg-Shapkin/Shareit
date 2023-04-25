package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.user.model.User;

public class UserDuplicateEmailException extends RuntimeException {
    public UserDuplicateEmailException(User user) {
        super(String.format("Пользователь с email:%s уже существует", user.getEmail()));
    }
}
