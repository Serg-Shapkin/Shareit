package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {

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

    private final ItemRequestDto itemRequestDto = ItemRequestDto
            .builder()
            .id(1L)
            .description("description")
            .requestor(user)
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    @Test
    @DisplayName("Item request to ItemRequestDTO")
    void testItemRequestToItemRequestDTO() {

        ItemRequestDto itemRequestDto1 = RequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequestDto1.getId(), itemRequestDto.getId());
        assertEquals(itemRequestDto1.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequestDto1.getRequestor(), itemRequestDto.getRequestor());
        assertEquals(itemRequestDto1.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    @DisplayName("ItemRequestDTO to item request")
    void testItemRequestDTOToItemRequest() {

        ItemRequest itemRequest1 = RequestMapper.toItemRequest(itemRequestDto);

        assertEquals(itemRequest1.getId(), itemRequest.getId());
        assertEquals(itemRequest1.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequest1.getRequestor(), itemRequest.getRequestor());
        assertEquals(itemRequest1.getCreated(), itemRequest.getCreated());
    }
}
