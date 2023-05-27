package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String HEADER = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(HEADER) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(HEADER) long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(HEADER) long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping(value = "/{bookingId}")
	public ResponseEntity<Object> updateBooking(@PathVariable(value = "bookingId") long bookingId,
										 @RequestHeader(HEADER) Long userId,
										 @RequestParam(value = "approved") Boolean approved) {
		log.info("Update booking {}, userId={}", bookingId, userId);
		return bookingClient.updateBooking(bookingId, userId, approved);
	}

	@GetMapping(value = "/owner")
	public ResponseEntity<Object> getBookingsByOwner(@RequestParam(value = "state", defaultValue = "ALL") String state,
													 @RequestHeader(HEADER) Long ownerId,
													 @PositiveOrZero
														 @RequestParam(name = "from", defaultValue = "0") int from,
													 @Positive
														 @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
		BookingState bookingState = BookingState.from(state)
						.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Get bookings by ownerId={}", ownerId);
		return bookingClient.getBookingsByOwner(bookingState, ownerId, from, size);
	}
}
