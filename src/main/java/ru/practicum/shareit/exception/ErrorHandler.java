package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

    // All
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка валидации данных", e.getMessage());
    }

    // Booking
    @ExceptionHandler({BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Бронирование не найдено", e.getMessage());
    }

    @ExceptionHandler({BookingCreateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleBookingCreateException(final BookingCreateException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка при создании бронирования", e.getMessage());
    }

    @ExceptionHandler({InvalidBookingException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleInvalidBookingException(final InvalidBookingException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка при бронировании", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public ErrorResponse handleUnsupportedStatusException(final IllegalArgumentException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    // Item
    @ExceptionHandler({ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Вещь не найдена", e.getMessage());
    }

    @ExceptionHandler({ItemNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleItemNotAvailableException(final ItemNotAvailableException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка статуса доступности вещи", e.getMessage());
    }

    @ExceptionHandler({ItemNotNameException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 404
    public ErrorResponse handleItemNotNameException(final ItemNotNameException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка в названии вещи", e.getMessage());
    }

    @ExceptionHandler({ItemNotDescriptionException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 404
    public ErrorResponse handleItemNotDescriptionException(final ItemNotDescriptionException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка в описании вещи", e.getMessage());
    }

    @ExceptionHandler({MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 404
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Отсутствует идентификатор пользователя X-Sharer-User-Id", e.getMessage());
    }

    @ExceptionHandler({CommentCreateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleCommentCreateException(final CommentCreateException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка создания комментария", e.getMessage());
    }

    // Request
    @ExceptionHandler({RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleRequestNotFoundException(final RequestNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Запрос не найден", e.getMessage());
    }

    // User
    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Пользователь не найден", e.getMessage());
    }

    @ExceptionHandler({UserCreateException.class})
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse handleUserCreateException(final UserCreateException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка при создании пользователя", e.getMessage());
    }
}
