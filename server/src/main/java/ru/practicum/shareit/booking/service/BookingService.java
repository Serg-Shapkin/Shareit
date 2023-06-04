package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingInputDto bookingInputDto, Long bookerId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getBookings(String state, Long userId, Integer from, Integer size);

    List<BookingDto> getBookingsByOwner(String state, Long ownerId, Integer from, Integer size);
}
