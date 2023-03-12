package ru.practicum.shareit.user.hundler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.UserDuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserErrorResponse;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public UserErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        log.warn(e.getMessage());
        return new UserErrorResponse("Пользователь не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public UserErrorResponse handleUserInvalidEmailException(final UserDuplicateEmailException e) {
        log.warn(e.getMessage());
        return new UserErrorResponse("Ошибка в адресе электронной почты", e.getMessage());
    }
}
