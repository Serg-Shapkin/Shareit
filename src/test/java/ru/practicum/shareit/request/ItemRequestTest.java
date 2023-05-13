package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemRequestTest {
    private final User user = User
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    private final ItemRequest itemRequest = ItemRequest
            .builder()
            .id(1L)
            .description("description")
            .requestor(user)
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    private final ItemRequest itemRequest2 = ItemRequest
            .builder()
            .id(1L)
            .description("description2")
            .requestor(user)
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    @Test
    @DisplayName("Item request equals test")
    void itemRequestEqualsTest() {
        assertEquals(itemRequest, itemRequest);
        assertNotEquals(null, itemRequest);
        assertEquals(itemRequest, itemRequest2);
    }

    @Test
    @DisplayName("Item request hash code test")
    void itemHashCodeTest() {
        assertEquals(Objects.hash(itemRequest.getId()), itemRequest.hashCode());
    }
}
