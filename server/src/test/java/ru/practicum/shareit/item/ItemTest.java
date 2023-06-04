package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemTest {

    private final Item item = Item
            .builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .ownerId(2L)
            .build();

    private final Item item2 = Item
            .builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .ownerId(2L)
            .build();

    @Test
    @DisplayName("Item equals test")
    void itemEqualsTest() {
        assertEquals(item, item);
        assertNotEquals(null, item);
        assertEquals(item, item2);
    }

    @Test
    @DisplayName("Item hash code test")
    void itemHashCodeTest() {
        assertEquals(Objects.hash(item.getId()), item.hashCode());
    }
}
