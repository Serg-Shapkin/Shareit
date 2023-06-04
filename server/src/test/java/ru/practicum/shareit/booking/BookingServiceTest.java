package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

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
