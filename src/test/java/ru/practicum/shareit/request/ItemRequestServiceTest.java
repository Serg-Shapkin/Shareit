package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private static int counter = 0;
    private UserDto newUserDto;

    private static UserDto generateUser() {
        return UserDto
                .builder()
                .name("name " + (++counter))
                .email("user" + counter + "@ya.ru")
                .build();
    }

    private static ItemRequestDto generateItemRequest() {
        return ItemRequestDto.builder()
                .description("looking for item 4")
                .build();
    }

    @BeforeEach
    void createDto() {
        newUserDto = userService.create(generateUser());
    }

    @Test
    @DisplayName("Create item request")
    void testCreateItemRequest() {
        ItemRequestDto newItemRequestDto = generateItemRequest();

        ItemRequestDto itemRequestDto = itemRequestService.create(newItemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 12, 0));

        assertEquals(newUserDto.getId(), itemRequestDto.getRequestor().getId());
        assertEquals(newItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }
}
