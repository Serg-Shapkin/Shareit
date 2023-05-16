package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class UtilGenerators {
    private static long counter;

    public static UserDto generateUserDto() {
        return UserDto
                .builder()
                .name("name " + (++counter))
                .email("user" + counter + "@ya.ru")
                .build();
    }

    public static ItemDto generateItemDto(long ownerId) {
        return ItemDto
                .builder()
                .name("item " + (++counter))
                .description("description " + counter)
                .available(true)
                .ownerId(ownerId)
                .build();
    }

    public static BookingInputDto generateBookingInputDto(long itemId) {
        return BookingInputDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    public static BookingInputDto generateBookingInputDto(long itemId, LocalDateTime start, LocalDateTime end) {
        return BookingInputDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
    }
}
