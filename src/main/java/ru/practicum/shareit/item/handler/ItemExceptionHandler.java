package ru.practicum.shareit.item.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.dto.ItemErrorResponse;
import ru.practicum.shareit.item.exception.*;

@RestControllerAdvice
@Slf4j
public class ItemExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ItemErrorResponse handleItemNotFoundException(final ItemNotFoundException e) {
        log.warn(e.getMessage());
        return new ItemErrorResponse("Вещь не найдена", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ItemErrorResponse handleItemNotAvailableException(final ItemNotAvailableException e) {
        log.warn(e.getMessage());
        return new ItemErrorResponse("Ошибка статуса доступности вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 404
    public ItemErrorResponse handleItemNotNameException(final ItemNotNameException e) {
        log.warn(e.getMessage());
        return new ItemErrorResponse("Ошибка в названии вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 404
    public ItemErrorResponse handleItemNotDescriptionException(final ItemNotDescriptionException e) {
        log.warn(e.getMessage());
        return new ItemErrorResponse("Ошибка в описании вещи", e.getMessage());
    }
}
