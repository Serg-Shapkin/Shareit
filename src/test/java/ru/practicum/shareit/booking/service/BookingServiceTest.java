package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
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
import static ru.practicum.shareit.UtilGenerators.generateBookingInputDto;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

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



    private final UserDto userDto2 = UserDto
            .builder()
            .id(301L)
            .email("example1@example.com")
            .name("name1")
            .build();
    private final UserDto userDto3 = UserDto
            .builder()
            .id(302L)
            .email("example2@example.com")
            .name("name2")
            .build();

    private final ItemDto itemDto1 = ItemDto
            .builder()
            .id(301L)
            .name("item1")
            .description("description1")
            .ownerId(userDto3.getId())
            .available(true)
            .build();

    @Test
    @DisplayName("Exception when create booking by owner item")
    void shouldExceptionWhenCreateBookingByOwnerItem() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2030, 12, 25, 12, 0, 0))
                .end(LocalDateTime.of(2030, 12, 26, 12, 0, 0))
                .build();

        InvalidBookingException exp = assertThrows(InvalidBookingException.class,
                () -> bookingService.create(bookingInputDto, ownerDto.getId()));

        assertEquals("Невозможно забронировать собственную вещь", exp.getMessage());
    }

    @Test
    @DisplayName("Exception when create booking and end before start")
    void shouldExceptionWhenCreateBookingAndEndBeforeStart() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2030, 12, 26, 12, 0, 0))
                .end(LocalDateTime.of(2030, 12, 25, 12, 0, 0))
                .build();

        BookingCreateException exp = assertThrows(BookingCreateException.class,
                () -> bookingService.create(bookingInputDto, newUserDto.getId()));
        assertEquals("Дата окончания бронирования не может быть раньше начала",
                exp.getMessage());
    }

    @Test
    @DisplayName("Cancel booking after update")
    void shouldCancelBookingAfterUpdate() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        BookingDto updatedBookingDto = bookingService.update(bookingDto.getId(),
                newUserDto.getId(), false);

        assertEquals(bookingDto.getId(), updatedBookingDto.getId());
        Assertions.assertEquals(Status.CANCELED, updatedBookingDto.getStatus());
    }

    @Test
    @DisplayName("Exception when booker approved booking")
    void shouldExceptionWhenBookerApprovedBooking() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        assertThrows(InvalidBookingException.class,
                () -> bookingService.update(bookingDto.getId(),
                        newUserDto.getId(), true));
    }

    @Test
    @DisplayName("Exception when  update approved booking")
    void shouldExceptionWhenUpdateApprovedBooking() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        BookingDto updatedBooking  = bookingService.update(bookingDto.getId(), ownerDto.getId(), true);
        System.out.println(updatedBooking.getStatus());
        BookingCreateException exp = assertThrows(BookingCreateException.class,
                () -> bookingService.update(bookingDto.getId(),
                        ownerDto.getId(), false));
        assertEquals("Решение по бронированию уже есть", exp.getMessage());
    }

    @Test
    @DisplayName("Set rejected status when update")
    void shouldSetRejectedStatusWhenUpdate() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        BookingDto updatedBookingDto = bookingService.update(bookingDto.getId(),
                ownerDto.getId(), false);

        assertEquals(bookingDto.getId(), updatedBookingDto.getId());
        assertEquals(Status.REJECTED, updatedBookingDto.getStatus());
    }

    @Test
    @DisplayName("Exception when approved and canceled")
    void shouldExceptionWhenApprovedAndCanceled() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        bookingService.update(bookingDto.getId(), newUserDto.getId(), false);

        InvalidBookingException exp = assertThrows(InvalidBookingException.class,
                () -> bookingService.update(bookingDto.getId(),
                        ownerDto.getId(), true));

        assertEquals("Бронирование отменено!", exp.getMessage());
    }

    @Test
    @DisplayName("Exception when booker try approve")
    void shouldExceptionWhenBookerTryApprove() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        UserDto userDto3 = UserDto
                .builder()
                .email("Email@email.ru")
                .name("name")
                .build();

        UserDto newUserDto1 = userService.create(userDto3);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        InvalidBookingException exp = assertThrows(InvalidBookingException.class,
                () -> bookingService.update(bookingDto.getId(),
                        newUserDto1.getId(), true));

        assertEquals("Подтвердить бронирование может только владелец", exp.getMessage());
    }

    @Test
    @DisplayName("Return waiting bookings")
    void shouldReturnWaitingBookings() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        List<BookingDto> result = bookingService.getBookings("WAITING", newUserDto.getId(), 0, 10);

        assertEquals(1, result.size());
        assertEquals(bookingDto.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Return past bookings")
    void shouldReturnPastBooking() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }

        List<BookingDto> result = bookingService.getBookings("PAST", newUserDto.getId(), 0, 10);

        assertEquals(1, result.size());
        assertEquals(bookingDto.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Return future bookings")
    void shouldReturnFutureBooking() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(10))
                .end(LocalDateTime.now().plusSeconds(11))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        List<BookingDto> result = bookingService.getBookings("FUTURE", newUserDto.getId(), 0, 10);

        assertEquals(1, result.size());
        assertEquals(bookingDto.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Exception when unknown state")
    void shouldExceptionWhenUnknownState() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookings("-", newUserDto.getId(), 0, 10));
    }

    @Test
    @DisplayName("Exception when start equals end")
    void shouldExceptionWhenStartEqualsEnd() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(10);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(localDateTime)
                .end(localDateTime)
                .build();

        BookingCreateException exp = assertThrows(BookingCreateException.class,
                () -> bookingService.create(bookingInputDto, newUserDto.getId()));

        assertEquals("Время начала и окончания бронирования не должны совпадать", exp.getMessage());
    }

    @Test
    @DisplayName("Get booking by id")
    void shouldReturnBooking() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        BookingDto savedBooking = bookingService.getById(
                bookingDto.getId(),
                newUserDto.getId()
        );

        assertEquals(bookingDto.getId(), savedBooking.getId());
    }

    @Test
    @DisplayName("Return waiting when get by owner")
    void shouldReturnWaitingWhenGetByOwner() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId()
        );

        List<BookingDto> bookings = bookingService.getBookingsByOwner("WAITING", ownerDto.getId(), 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto.getId(), bookings.get(0).getId());
    }

    @Test
    @DisplayName("Return past when get by owner")
    void shouldReturnPastWhenGetByOwner() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);
        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }

        List<BookingDto> bookings = bookingService.getBookingsByOwner("PAST", ownerDto.getId(), 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto.getId(), bookings.get(0).getId());
    }

    @Test
    @DisplayName("Return future when get by owner")
    void shouldReturnFutureWhenGetByOwner() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(10))
                .end(LocalDateTime.now().plusSeconds(20))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());

        List<BookingDto> bookings = bookingService.getBookingsByOwner("FUTURE", ownerDto.getId(), 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(bookingDto.getId(), bookings.get(0).getId());
    }

    @Test
    @DisplayName("Exception when unknown state when get by owner")
    void shouldExceptionWhenUnknownStateWhenGetByOwner() {
        UserDto ownerDto = userService.create(userDto3);
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsByOwner("-", ownerDto.getId(), 0, 10));
    }

    @Test
    @DisplayName("Exception when add booking and not found user")
    void shouldExceptionWhenAddBookingAndNotFoundUser() {
        UserDto ownerDto = userService.create(userDto3);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(10))
                .end(LocalDateTime.now().plusSeconds(20))
                .build();

        assertThrows(UserNotFoundException.class,
                () -> bookingService.create(bookingInputDto, 666L));
    }

    @Test
    @DisplayName("Exception when add booking and item not available")
    void shouldExceptionWhenAddBookingAndItemNotAvailable() {
        UserDto ownerDto = userService.create(userDto3);
        itemDto1.setAvailable(false);

        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);
        itemDto1.setAvailable(true);

        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3))
                .build();

        assertThrows(BookingCreateException.class,
                () -> bookingService.create(bookingInputDto, newUserDto.getId()));
    }

    @Test
    @DisplayName("Exception when add booking and item not exist")
    void shouldExceptionWhenAddBookingAndItemNotExist() {
        UserDto newUserDto = userService.create(userDto2);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(666L)
                .start(LocalDateTime.now().plusSeconds(10))
                .end(LocalDateTime.now().plusSeconds(20))
                .build();

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(bookingInputDto, newUserDto.getId()));
    }

    @Test
    @DisplayName("Exception when get booking by not owner or not booker")
    void shouldExceptionWhenGetBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);

        UserDto userDto3 = UserDto
                .builder()
                .id(303L)
                .name("name3")
                .email("example3@example.ru")
                .build();

        userDto3 = userService.create(userDto3);
        Long userId = userDto3.getId();
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2030, 12, 25, 12, 0, 0))
                .end(LocalDateTime.of(2030, 12, 26, 12, 0, 0))
                .build();

        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getById(bookingDto.getId(), userId));
    }

    @Test
    @DisplayName("Return bookings when get bookings by booker and size is not null")
    void shouldReturnBookingsWhenGetBookingsByBookerAndSizeIsNotNull() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 13, 0, 0))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());

        BookingInputDto bookingInputDto1 = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 15, 0, 0))
                .build();

        bookingService.create(bookingInputDto1, newUserDto.getId());

        List<BookingDto> listBookings = bookingService.getBookings("ALL", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    @DisplayName("Return bookings when get bookings in waiting status by booker and size not null")
    void shouldReturnBookingsWhenGetBookingsInWaitingStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 13, 0, 0))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());

        BookingInputDto bookingInputDto1 = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 15, 0, 0))
                .build();

        bookingService.create(bookingInputDto1, newUserDto.getId());

        List<BookingDto> listBookings = bookingService.getBookings("WAITING", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    @DisplayName("Return bookings when get bookings in rejected status byBooker and size not null")
    void shouldReturnBookingsWhenGetBookingsInRejectedStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 13, 0, 0))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());

        BookingInputDto bookingInputDto1 = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 15, 0, 0))
                .build();

        bookingService.create(bookingInputDto1, newUserDto.getId());

        List<BookingDto> listBookings = bookingService.getBookings("REJECTED", newUserDto.getId(), 0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    @DisplayName("Return bookings when get bookings by owner and size not null")
    void shouldReturnBookingsWhenGetBookingsByOwnerAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 13, 0, 0))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 15, 0, 0))
                .build();

        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsByOwner("ALL", ownerDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    @DisplayName("Return bookings when get bookings by owner and status waiting and size not null")
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusWaitingAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 13, 0, 0))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());

        BookingInputDto bookingInputDto1 = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 15, 0, 0))
                .build();

        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsByOwner("WAITING", ownerDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    @DisplayName("Return bookings when get bookings byOwner and status rejected and size not null")
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusRejectedAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto3);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto1);

        BookingInputDto bookingInputDto = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 13, 0, 0))
                .build();

        bookingService.create(bookingInputDto, newUserDto.getId());

        BookingInputDto bookingInputDto1 = BookingInputDto
                .builder()
                .itemId(newItemDto.getId())
                .start(LocalDateTime.of(2025, 1, 1, 14, 0, 0))
                .end(LocalDateTime.of(2025, 1, 1, 15, 0, 0))
                .build();

        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsByOwner("REJECTED", ownerDto.getId(), 0, 1);
        assertEquals(0, listBookings.size());
    }
}
