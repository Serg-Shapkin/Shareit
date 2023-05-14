package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingShortDtoTest {

    @Autowired
    private JacksonTester<BookingShortDto> dtoJacksonTester;

    private final BookingShortDto bookingShortDto = BookingShortDto
            .builder()
            .id(1L)
            .bookerId(2L)
            .start(LocalDateTime.of(2025, 01, 01, 12, 00))
            .end(LocalDateTime.of(2025, 01, 01, 13, 00))
            .build();

    @Test
    @DisplayName("Booking short DTO test")
    void testBookingShortDto() throws IOException {
        JsonContent<BookingShortDto> result = dtoJacksonTester.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T13:00:00");
    }

    @Test
    @DisplayName("Booking short DTO set time and booker id")
    void testBookingInputDTOSetTimeAndItemId() {
        LocalDateTime start = bookingShortDto.getStart();
        LocalDateTime end = bookingShortDto.getEnd();

        bookingShortDto.setBookerId(3L);
        bookingShortDto.setStart(start);
        bookingShortDto.setEnd(end);

        assertEquals(1L, bookingShortDto.getId());
        assertEquals(3L, bookingShortDto.getBookerId());
        assertEquals(start, bookingShortDto.getStart());
        assertEquals(end, bookingShortDto.getEnd());
    }
}
