package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingInputDto bookingInputDto,
                             @RequestHeader(HEADER) Long bookerId) {
        log.info("Получен запрос к эндпоинту:{} /bookings", "POST");
        return bookingService.create(bookingInputDto, bookerId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto update(@PathVariable(value = "bookingId") Long bookingId,
                             @RequestHeader(HEADER) Long userId,
                             @RequestParam(value = "approved") Boolean approved) {
        log.info("Получен запрос к эндпоинту:{} /bookings/{}", "PATCH", bookingId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDto getById(@PathVariable(value = "bookingId") Long bookingId,
                              @RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /bookings/{}", "GET", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                        @RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /bookings", "GET");
        return bookingService.getBookings(state, userId);
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                              @RequestHeader(HEADER) Long ownerId) {
        log.info("Получен запрос к эндпоинту:{} /bookings/owner", "GET");
        return bookingService.getBookingsByOwner(state, ownerId);
    }
}
