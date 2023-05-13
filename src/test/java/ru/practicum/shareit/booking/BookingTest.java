package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    User owner = User
            .builder()
            .id(1L)
            .name("OwnerName")
            .email("owner@email.com")
            .build();

    User booker = User
            .builder()
            .id(2L)
            .name("BookerName")
            .email("booker@email.com")
            .build();

    Item item = Item
            .builder()
            .id(1L)
            .name("ItemName")
            .description("itemDescription")
            .ownerId(owner.getId())
            .available(true)
            .build();

    Booking booking = Booking
            .builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .item(item)
            .booker(booker)
            .build();

    Booking booking2 = Booking
            .builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .item(item)
            .booker(booker)
            .build();

    @Test
    @DisplayName("Booking set start and end date time")
    void testBookingSetStartAndEndDateTime() {
        booking.setStart(LocalDateTime.of(2025, 1, 1, 12, 0));
        booking.setEnd(LocalDateTime.of(2025, 1, 1, 13, 0));

        assertEquals("2025-01-01T12:00", booking.getStart().toString());
        assertEquals("2025-01-01T13:00", booking.getEnd().toString());
    }

    @Test
    @DisplayName("Booking equals test")
    void bookingEqualsTest() {
        assertEquals(booking, booking2);
        assertNotEquals(null, booking);
        assertEquals(booking, booking2);
    }

    @Test
    @DisplayName("Booking hash code test")
    void bookingHashCodeTest() {
        assertEquals(Objects.hash(booking.getId()), booking.hashCode());
    }
}
