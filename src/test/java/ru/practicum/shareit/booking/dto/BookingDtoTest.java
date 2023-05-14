package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> dtoJacksonTester;

    private final UserDto userOwnerDto = UserDto
            .builder()
            .id(1L)
            .name("OwnerName")
            .email("owner@mail.ru")
            .build();

    private final UserDto userBookerDto = UserDto
            .builder()
            .id(1L)
            .name("BookerName")
            .email("booker@mail.ru")
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("Description")
            .available(true)
            .ownerId(userOwnerDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final BookingDto bookingDto = BookingDto
            .builder()
            .id(1L)
            .start(LocalDateTime.of(2025, 01, 01, 12, 00))
            .end(LocalDateTime.of(2025, 01, 01, 13, 00))
            .item(itemDto)
            .booker(userBookerDto)
            .status(Status.WAITING)
            .build();

    @Test
    @DisplayName("Booking DTO test")
    void testBookingDto() throws IOException {
        JsonContent<BookingDto> result = dtoJacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T13:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    @DisplayName("Booking set id")
    void testBookingDtoSetId() {
        Long id = 100L;
        bookingDto.setId(id);

        assertEquals(id, bookingDto.getId());

        bookingDto.setId(1L);
    }

    @Test
    @DisplayName("Booking DTO set start and end date time")
    void testBookingDtoSetStartAndEndDateTime() {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime newDate = LocalDateTime.of(2035, 01,01,12,00);

        bookingDto.setStart(newDate);
        bookingDto.setEnd(newDate);
        bookingDto.setItem(itemDto);


        assertEquals(newDate, bookingDto.getStart());
        assertEquals(newDate, bookingDto.getEnd());

        bookingDto.setStart(start);
        bookingDto.setEnd(end);
    }
}
