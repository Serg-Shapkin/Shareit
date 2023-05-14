package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> dtoJacksonTester;

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("Description")
            .available(true)
            .ownerId(3L)
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final ItemDto itemDto2 = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("Description")
            .available(true)
            .ownerId(3L)
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    @Test
    @DisplayName("Item DTO test")
    void testItemDto() throws IOException {
        JsonContent<ItemDto> result = dtoJacksonTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ItemName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(3);
    }

    @Test
    @DisplayName("Item DTO set request and id test")
    void testItemDtoSetRequestAndId() {
        itemDto.setRequestId(5L);
        itemDto.setId(10L);

        assertEquals(5L, itemDto.getRequestId());
        assertEquals(10L, itemDto.getId());
    }

    @Test
    @DisplayName("Item DTO equals test")
    void itemEqualsTest() {
        assertEquals(itemDto, itemDto);
        assertNotEquals(null, itemDto);
        assertEquals(itemDto, itemDto2);
    }

    @Test
    @DisplayName("Item DTO hash code test")
    void itemHashCodeTest() {
        assertEquals(Objects.hash(itemDto.getId()), itemDto.hashCode());
    }

}
