package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.booking.BookingCreateException;
import ru.practicum.shareit.exception.booking.BookingNotFoundException;
import ru.practicum.shareit.exception.booking.InvalidBookingException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.UtilGenerators.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceUnitTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private UserDto ownerUser;
    private UserDto bookerUser;
    private ItemDto itemDto;

    @BeforeEach
    void createDto() {
        ownerUser = userService.create(generateUserDto());
        bookerUser = userService.create(generateUserDto());
        itemDto = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
    }

    @Test
    @DisplayName("Create booking")
    void testCreateBooking() {
        BookingDto booking = bookingService.create(generateBookingInputDto(itemDto.getId()), bookerUser.getId());
        assertEquals(bookerUser.getId(), booking.getBooker().getId());
        assertEquals(Status.WAITING, booking.getStatus());
        assertEquals(itemDto.getId(), booking.getItem().getId());
    }

    @Test
    @DisplayName("Create booking with error params")
    void testCreateBookingWithErrorParams() {
        assertThrows(UserNotFoundException.class, () -> bookingService.create(generateBookingInputDto(-1), -1L));
        assertThrows(ItemNotFoundException.class, () -> bookingService.create(generateBookingInputDto(-1), bookerUser.getId()));
        assertThrows(InvalidBookingException.class, () -> bookingService.create(generateBookingInputDto(itemDto.getId()), ownerUser.getId()));

        itemDto.setAvailable(false);
        itemService.update(itemDto, itemDto.getId(), ownerUser.getId());
        assertThrows(BookingCreateException.class, () -> bookingService.create(generateBookingInputDto(itemDto.getId()), ownerUser.getId()));
    }

    @Test
    @DisplayName("Update booking")
    void testUpdateBooking() {
        BookingDto booking1 = bookingService.create(generateBookingInputDto(itemDto.getId()), bookerUser.getId());
        booking1 = bookingService.update(booking1.getId(), ownerUser.getId(), true);
        assertEquals(Status.APPROVED, booking1.getStatus());

        ItemDto itemDto2 = itemService.create(ownerUser.getId(), generateItemDto(ownerUser.getId()));
        BookingDto booking2 = bookingService.create(generateBookingInputDto(itemDto2.getId()), bookerUser.getId());
        booking2 = bookingService.update(booking2.getId(), ownerUser.getId(), false);
        assertEquals(Status.REJECTED, booking2.getStatus());
    }

    @Test
    @DisplayName("Failed Approve and reject booking")
    void testFailedApproveAndRejectBooking() {
        BookingDto booking = bookingService.create(generateBookingInputDto(itemDto.getId()), bookerUser.getId());
        assertThrows(InvalidBookingException.class, () -> bookingService.update(booking.getId(), bookerUser.getId(), true));

        bookingService.update(booking.getId(), ownerUser.getId(), true);
        assertThrows(InvalidBookingException.class, () -> bookingService.update(booking.getId(), bookerUser.getId(), true));
    }

    @Test
    @DisplayName("Get booking by id")
    void testGetBookingById() {
        BookingDto booking = bookingService.create(generateBookingInputDto(itemDto.getId()), bookerUser.getId());
        BookingDto bookingResult = bookingService.getById(booking.getId(), ownerUser.getId());
        assertEquals(booking, bookingResult);

        bookingResult = bookingService.getById(booking.getId(), bookerUser.getId());
        assertEquals(booking, bookingResult);
    }

    @Test
    @DisplayName("Get booking by id request from not owner and not booker")
    void testGetBookingByIdRequestFromNotOwnerAndNotBooker() {
        BookingDto booking = bookingService.create(generateBookingInputDto(itemDto.getId()), bookerUser.getId());
        UserDto userDto = userService.create(generateUserDto());
        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(booking.getId(), userDto.getId()));
    }

    @Test
    @DisplayName("Get bookings")
    void testDifferentStatesGetAllBookingsByUserId() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto booking1 = bookingService.create(generateBookingInputDto(itemDto.getId(), now.plusHours(1), now.plusHours(2)),
                bookerUser.getId());
        BookingDto booking2 = bookingService.create(generateBookingInputDto(itemDto.getId(), now.plusHours(3), now.plusHours(4)),
                bookerUser.getId());

        BookingDto booking3 = createAndApproveBooking(itemDto.getId(), now.plusHours(5), now.plusHours(6),
                bookerUser.getId(), ownerUser.getId(), false);

        BookingDto booking4 = createAndApproveBooking(itemDto.getId(), now.plusHours(7), now.plusHours(8),
                bookerUser.getId(), ownerUser.getId(), false);

        BookingDto booking5 = createAndApproveBooking(itemDto.getId(), now.plusHours(9), now.plusHours(10),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking6 = createAndApproveBooking(itemDto.getId(), now.plusHours(11), now.plusHours(12),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking7 = createAndApproveBooking(itemDto.getId(), now.plusHours(13), now.plusHours(14),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking8 = createAndApproveBooking(itemDto.getId(), now.plusHours(15), now.plusHours(16),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking9 = createAndApproveBooking(itemDto.getId(), now.plusHours(17), now.plusHours(18),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking10 = createAndApproveBooking(itemDto.getId(), now.plusMinutes(30), now.plusHours(20),
                bookerUser.getId(), ownerUser.getId(), true);

        List<BookingDto> bookingDtoList = bookingService.getBookings(State.ALL.name(), bookerUser.getId(), 0, 10);
        assertEquals(10, bookingDtoList.size());
        assertEquals(List.of(booking9.getId(), booking8.getId(), booking7.getId(), booking6.getId(), booking5.getId(), booking4.getId(),
                booking3.getId(), booking2.getId(), booking1.getId(), booking10.getId()), bookingDtoList.stream()
                .map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookings(State.ALL.name(), bookerUser.getId(), 0, 7);
        assertEquals(7, bookingDtoList.size());
        assertEquals(List.of(booking9.getId(), booking8.getId(), booking7.getId(), booking6.getId(), booking5.getId(), booking4.getId(), booking3.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookings(State.ALL.name(), bookerUser.getId(), 7, 7);
        assertEquals(3, bookingDtoList.size());
        assertEquals(List.of(booking2.getId(), booking1.getId(), booking10.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookings(State.WAITING.name(), bookerUser.getId(), 0, 10);
        assertEquals(2, bookingDtoList.size());
        assertEquals(List.of(booking2.getId(), booking1.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookings(State.REJECTED.name(), bookerUser.getId(), 0, 10);
        assertEquals(2, bookingDtoList.size());
        assertEquals(List.of(booking4.getId(), booking3.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookings(State.PAST.name(), bookerUser.getId(), 0, 10);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getBookings(State.CURRENT.name(), bookerUser.getId(), 0, 10);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getBookings(State.FUTURE.name(), bookerUser.getId(), 0, 10);
        assertEquals(10, bookingDtoList.size());
    }

    @Test
    @DisplayName("Get bookings by owner")
    void testDifferentStatesGetAllBookingsByOwnerId() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto booking1 = bookingService.create(generateBookingInputDto(itemDto.getId(), now.plusHours(1), now.plusHours(2)),
                bookerUser.getId());
        BookingDto booking2 = bookingService.create(generateBookingInputDto(itemDto.getId(), now.plusHours(3), now.plusHours(4)),
                bookerUser.getId());

        BookingDto booking3 = createAndApproveBooking(itemDto.getId(), now.plusHours(5), now.plusHours(6),
                bookerUser.getId(), ownerUser.getId(), false);

        BookingDto booking4 = createAndApproveBooking(itemDto.getId(), now.plusHours(7), now.plusHours(8),
                bookerUser.getId(), ownerUser.getId(), false);

        BookingDto booking5 = createAndApproveBooking(itemDto.getId(), now.plusHours(9), now.plusHours(10),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking6 = createAndApproveBooking(itemDto.getId(), now.plusHours(11), now.plusHours(12),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking7 = createAndApproveBooking(itemDto.getId(), now.plusHours(13), now.plusHours(14),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking8 = createAndApproveBooking(itemDto.getId(), now.plusHours(15), now.plusHours(16),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking9 = createAndApproveBooking(itemDto.getId(), now.plusHours(17), now.plusHours(18),
                bookerUser.getId(), ownerUser.getId(), true);

        BookingDto booking10 = createAndApproveBooking(itemDto.getId(), now.plusHours(1), now.plusHours(20),
                bookerUser.getId(), ownerUser.getId(), true);

        List<BookingDto> bookingDtoList = bookingService.getBookingsByOwner(State.ALL.name(), ownerUser.getId(), 0, 10);
        assertEquals(10, bookingDtoList.size());
        assertEquals(List.of(booking9.getId(), booking8.getId(), booking7.getId(), booking6.getId(), booking5.getId(), booking4.getId(),
                booking3.getId(), booking2.getId(), booking1.getId(), booking10.getId()), bookingDtoList.stream()
                .map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookingsByOwner(State.ALL.name(), ownerUser.getId(), 0, 7);
        assertEquals(7, bookingDtoList.size());
        assertEquals(List.of(booking9.getId(), booking8.getId(), booking7.getId(), booking6.getId(), booking5.getId(), booking4.getId(), booking3.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookingsByOwner(State.WAITING.name(), ownerUser.getId(), 0, 10);
        assertEquals(2, bookingDtoList.size());
        assertEquals(List.of(booking2.getId(), booking1.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookingsByOwner(State.REJECTED.name(), ownerUser.getId(), 0, 10);
        assertEquals(2, bookingDtoList.size());
        assertEquals(List.of(booking4.getId(), booking3.getId()),
                bookingDtoList.stream().map(BookingDto::getId).collect(Collectors.toList()));

        bookingDtoList = bookingService.getBookingsByOwner(State.PAST.name(), ownerUser.getId(), 0, 10);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getBookingsByOwner(State.CURRENT.name(), ownerUser.getId(), 0, 10);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getBookingsByOwner(State.FUTURE.name(), ownerUser.getId(), 0, 10);
        assertEquals(10, bookingDtoList.size());

    }

    private BookingDto createAndApproveBooking(long itemId, LocalDateTime start, LocalDateTime end, long bookerId, long ownerId, boolean approved) {
        BookingDto bookingDto = bookingService.create(new BookingInputDto(itemId, start, end), bookerId);
        return bookingService.update(bookingDto.getId(), ownerId, approved);
    }
}
