package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> dtoJacksonTester;

    private final User user = User
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto
            .builder()
            .id(1L)
            .description("description")
            .requestor(user)
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

    @Test
    @DisplayName("Booking request DTO test")
    void testItemRequestDto() throws IOException {
        JsonContent<ItemRequestDto> result = dtoJacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-01T12:00:00");
    }

    @Test
    @DisplayName("Item request set test")
    void testItemRequestSetParameters() {
        LocalDateTime created = itemRequestDto.getCreated();

        itemRequestDto.setDescription("description");
        itemRequestDto.setRequestor(user);
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(created);

        assertEquals(1L, itemRequestDto.getId());
        assertEquals("description", itemRequestDto.getDescription());
        assertEquals(user, itemRequestDto.getRequestor());
        assertEquals(created, itemRequestDto.getCreated());
    }
}
