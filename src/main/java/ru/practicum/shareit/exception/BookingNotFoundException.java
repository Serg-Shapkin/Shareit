package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super(String.format("Бронирование с id: %s не найдено", id));
    }

    public BookingNotFoundException(final String message) {
        super(message);
    }
}
