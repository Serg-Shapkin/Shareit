package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private final Item item = Item
            .builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .ownerId(2L)
            .requestId(3L)
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .ownerId(2L)
            .requestId(3L)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    @Test
    @DisplayName("Item to ItemDTO")
    void testItemToItemDto() {
        ItemDto itemDto1 = ItemMapper.toItemDto(item);

        assertEquals(itemDto1.getId(), itemDto.getId());
        assertEquals(itemDto1.getName(), itemDto.getName());
        assertEquals(itemDto1.getDescription(), itemDto.getDescription());
        assertEquals(itemDto1.getAvailable(), itemDto.getAvailable());
        assertEquals(itemDto1.getOwnerId(), itemDto.getOwnerId());
        assertEquals(itemDto1.getRequestId(), itemDto.getRequestId());
    }

    @Test
    @DisplayName("ItemDTO to Item")
    void testItemDtoToItem() {
        Item item1 = ItemMapper.toItem(itemDto);

        assertEquals(item1.getId(), item.getId());
        assertEquals(item1.getName(), item.getName());
        assertEquals(item1.getDescription(), item.getDescription());
        assertEquals(item1.getAvailable(), item.getAvailable());
        assertEquals(item1.getOwnerId(), item.getOwnerId());
        assertEquals(item1.getRequestId(), item.getRequestId());
    }
}
