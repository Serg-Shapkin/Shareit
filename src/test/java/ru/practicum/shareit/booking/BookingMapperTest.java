package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    private final User booker = User
            .builder()
            .id(1L)
            .name("booker")
            .email("booker@mail.ru")
            .build();

    private final Item item = Item
            .builder()
            .id(1L)
            .name("item")
            .description("itemDescription")
            .ownerId(2L)
            .available(true)
            .build();

    private final Booking booking = Booking
            .builder()
            .id(1L)
            .start(LocalDateTime.of(2025, 1, 1, 12, 0))
            .end(LocalDateTime.of(2025, 1, 1, 13, 0))
            .item(item)
            .booker(booker)
            .status(Status.WAITING)
            .build();

    private final UserDto userBookerDto = UserDto
            .builder()
            .id(1L)
            .name("booker")
            .email("booker@mail.ru")
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("item")
            .description("itemDescription")
            .available(true)
            .ownerId(2L)
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final BookingDto bookingDto = BookingDto
            .builder()
            .id(1L)
            .start(LocalDateTime.of(2025, 1, 1, 12, 0))
            .end(LocalDateTime.of(2025, 1, 1, 13, 0))
            .item(itemDto)
            .booker(userBookerDto)
            .status(Status.WAITING)
            .build();

    private final BookingInputDto bookingInputDto = BookingInputDto
            .builder()
            .itemId(itemDto.getId())
            .start(bookingDto.getStart())
            .end(bookingDto.getEnd())
            .build();

    private final BookingShortDto bookingShortDto = BookingShortDto
            .builder()
            .id(1L)
            .bookerId(1L)
            .start(LocalDateTime.of(2025, 1, 1, 12, 0))
            .end(LocalDateTime.of(2025, 1, 1, 13, 0))
            .build();

    @Test
    @DisplayName("Booking to BookingDTO")
    void testBookingToBookingDto() {
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking);

        assertEquals(bookingDto1.getId(), bookingDto.getId());
        assertEquals(bookingDto1.getStart(), bookingDto.getStart());
        assertEquals(bookingDto1.getEnd(), bookingDto.getEnd());

        assertEquals(bookingDto1.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(bookingDto1.getItem().getName(), bookingDto.getItem().getName());
        assertEquals(bookingDto1.getItem().getDescription(), bookingDto.getItem().getDescription());
        assertEquals(bookingDto1.getItem().getAvailable(), bookingDto.getItem().getAvailable());
        assertEquals(bookingDto1.getItem().getOwnerId(), bookingDto.getItem().getOwnerId());

        assertEquals(bookingDto1.getBooker(), bookingDto.getBooker());
        assertEquals(bookingDto1.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(bookingDto1.getBooker().getName(), bookingDto.getBooker().getName());
        assertEquals(bookingDto1.getBooker().getEmail(), bookingDto.getBooker().getEmail());

        assertEquals(bookingDto1.getStatus(), bookingDto.getStatus());
    }

    @Test
    @DisplayName("BookingDTO to Booking")
    void testBookingDTOToBooking() {
        Booking booking1 = BookingMapper.toBooking(bookingInputDto, booker, item);

        assertEquals(booking1.getStart(), booking.getStart());
        assertEquals(booking1.getEnd(), booking.getEnd());

        assertEquals(booking1.getItem(), booking.getItem());
        assertEquals(booking1.getItem().getId(), booking.getItem().getId());
        assertEquals(booking1.getItem().getName(), booking.getItem().getName());
        assertEquals(booking1.getItem().getDescription(), booking.getItem().getDescription());
        assertEquals(booking1.getItem().getAvailable(), booking.getItem().getAvailable());
        assertEquals(booking1.getItem().getOwnerId(), booking.getItem().getOwnerId());

        assertEquals(booking1.getBooker(), booking.getBooker());
        assertEquals(booking1.getBooker().getId(), booking.getBooker().getId());
        assertEquals(booking1.getBooker().getName(), booking.getBooker().getName());
        assertEquals(booking1.getBooker().getEmail(), booking.getBooker().getEmail());
    }

    @Test
    @DisplayName("Booking to BookingShortDTO")
    void testBookingToBookingShortDto() {
        BookingShortDto bookingShortDto1 = BookingMapper.toBookingShortDto(booking);

        assertEquals(bookingShortDto1.getId(), bookingShortDto.getId());
        assertEquals(bookingShortDto1.getBookerId(), bookingShortDto.getBookerId());
        assertEquals(bookingShortDto1.getStart(), bookingShortDto.getStart());
        assertEquals(bookingShortDto1.getEnd(), bookingShortDto.getEnd());
    }
}
