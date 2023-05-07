package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.dto.BookingErrorResponse;
import ru.practicum.shareit.booking.exception.BookingCreateException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidBookingException;

@RestControllerAdvice
@Slf4j
public class BookingExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public BookingErrorResponse handleBookingNotFoundException(final BookingNotFoundException e) {
        log.warn(e.getMessage());
        return new BookingErrorResponse("Бронирование не найдено", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public BookingErrorResponse handleBookingCreateException(final BookingCreateException e) {
        log.warn(e.getMessage());
        return new BookingErrorResponse("Ошибка при создании бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public BookingErrorResponse handleInvalidBookingException(final InvalidBookingException e) {
        log.warn(e.getMessage());
        return new BookingErrorResponse("Ошибка при бронировании", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public BookingErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new BookingErrorResponse("Ошибка валидации данных бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public BookingErrorResponse handleUnsupportedStatusException(final IllegalArgumentException e) {
        log.warn(e.getMessage());
        return new BookingErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }
}
