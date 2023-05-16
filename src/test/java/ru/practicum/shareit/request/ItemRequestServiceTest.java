package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.request.RequestNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;

    private final UserService userService;

    private final UserDto userDto1 = UserDto
            .builder()
            .id(101L)
            .name("UserTest1")
            .email("userTes1t@mail.ru")
            .build();
    private final UserDto userDto2 = UserDto
            .builder()
            .id(102L)
            .name("UserTest2")
            .email("userTest2@mail.ru")
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto
            .builder()
            .id(100L)
            .description("ItemRequest description")
            .requestor(UserMapper.toUser(userDto1))
            .created(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();


    @Test
    @DisplayName("Create item request")
    void testCreateItemRequest() {
        UserDto newUserDto = userService.create(userDto1);
        ItemRequestDto returnRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 12, 0));

        assertThat(returnRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    @DisplayName("Exception when create item request with wrong user id")
    void testExceptionWhenCreateItemRequestWithWrongUserId() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, -2L,
                        LocalDateTime.of(2025, 1, 1, 12, 0)));
    }

    @Test
    @DisplayName("Exception when get item request with wrong id")
    void testExceptionWhenGetItemRequestWithWrongId() {
        UserDto firstUserDto = userService.create(userDto1);
        assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getById(-2L, firstUserDto.getId()));
    }

    @Test
    @DisplayName("Return all item request when size not null and null")
    void testReturnAllItemRequestsWhenSizeNotNullAndNull() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 12, 0));
        itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 13, 0));
        List<ItemRequestDto> listItemRequest = itemRequestService.getAll(firstUserDto.getId(),
                0, 10);
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    @DisplayName("Get all by user")
    void testGetAllByUser() {
        userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 12, 0));
        itemRequestService.create(itemRequestDto, newUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 13, 0));
        List<ItemRequestDto> listItemRequest = itemRequestService.getAllByUser(newUserDto.getId());
        System.out.println(listItemRequest.toString());
        assertThat(listItemRequest.size(), equalTo(2));
    }

/*    @Test
    @DisplayName("Get by id")
    void testGetRequestById() {
        UserDto firstUserDto = userService.create(userDto1);
        ItemRequestDto newItemRequestDto = itemRequestService.create(itemRequestDto, firstUserDto.getId(),
                LocalDateTime.of(2025, 1, 1, 12, 0));
        ItemRequestDto returnItemRequestDto = itemRequestService.getById(newItemRequestDto.getId(),
                firstUserDto.getId());
        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }*/
}
