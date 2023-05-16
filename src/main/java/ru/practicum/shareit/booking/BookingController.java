package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
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
                                        @RequestHeader(HEADER) Long userId,
                                        @PositiveOrZero
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @Positive
                                        @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        log.info("Получен запрос к эндпоинту:{} /bookings", "GET");
        return bookingService.getBookings(state, userId, from, size);
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader(HEADER) Long ownerId,
                                               @PositiveOrZero
                                               @RequestParam(name = "from", defaultValue = "0") int from,
                                               @Positive
                                               @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        log.info("Получен запрос к эндпоинту:{} /bookings/owner", "GET");
        return bookingService.getBookingsByOwner(state, ownerId, from, size);
    }
}
