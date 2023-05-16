package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingInputDtoTest {

    @Autowired
    private JacksonTester<BookingInputDto> dtoJacksonTester;

    private final BookingInputDto bookingInputDto = BookingInputDto
            .builder()
            .itemId(1L)
            .start(LocalDateTime.of(2025, 01, 01, 12, 00))
            .end(LocalDateTime.of(2025, 01, 01, 13, 00))
            .build();

    @Test
    @DisplayName("Booking input DTO test")
    void testBookingInputDto() throws IOException {
        JsonContent<BookingInputDto> result = dtoJacksonTester.write(bookingInputDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T13:00:00");
    }

    @Test
    @DisplayName("Booking input DTO set time and item id")
    void testBookingInputDTOSetTimeAndItemId() {
        LocalDateTime start = bookingInputDto.getStart();
        LocalDateTime end = bookingInputDto.getEnd();

        bookingInputDto.setItemId(1L);
        bookingInputDto.setStart(start);
        bookingInputDto.setEnd(end);

        assertEquals(1L, bookingInputDto.getItemId());
        assertEquals(start, bookingInputDto.getStart());
        assertEquals(end, bookingInputDto.getEnd());
    }
}
