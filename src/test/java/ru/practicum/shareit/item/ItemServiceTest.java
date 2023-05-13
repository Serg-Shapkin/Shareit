package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.comment.CommentCreateException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("userDto")
            .email("userDto@mail.ru")
            .build();

    private final UserDto userDto1 = UserDto
            .builder()
            .id(2L)
            .name("userDto1")
            .email("userDto1@mail.ru")
            .build();

    private final UserDto userDto2 = UserDto
            .builder()
            .id(3L)
            .name("userDto2")
            .email("userDto2@mail.ru")
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("itemDto")
            .description("Description")
            .available(true)
            .ownerId(userDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final ItemDto itemDto1 = ItemDto
            .builder()
            .id(2L)
            .name("itemDto1")
            .description("Description1")
            .available(true)
            .ownerId(userDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    @Test
    @DisplayName("Create item")
    void testCreateItem() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(newUserDto.getId(), itemDto);
        ItemDto returnItemDto = itemService.getById(newItemDto.getId(), newUserDto.getId());

        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    @DisplayName("Get item by id")
    void testGetItemById() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(newUserDto.getId(), itemDto);
        ItemDto returnItemDto = itemService.getById(newItemDto.getId(), newUserDto.getId());

        assertThat(returnItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    @DisplayName("Update item")
    void testUpdateItem() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(newUserDto.getId(), itemDto);

        newItemDto.setName("NewName");
        newItemDto.setDescription("NewDescription");
        newItemDto.setAvailable(false);

        ItemDto returnItemDto = itemService.update(newItemDto, newItemDto.getId(), newUserDto.getId());

        assertThat(returnItemDto.getName(), equalTo("NewName"));
        assertThat(returnItemDto.getDescription(), equalTo("NewDescription"));
        assertFalse(returnItemDto.getAvailable());
    }

    @Test
    @DisplayName("Exception when update item not owner")
    void testExceptionWhenUpdateItemNotOwner() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto);

        assertThrows(ItemNotFoundException.class,
                () -> itemService.update(newItemDto, newItemDto.getId(), newUserDto.getId()));
    }

    @Test
    @DisplayName("Delete item when user not owner")
    void testDeleteItemWhenUserNotOwner() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto);

        assertThrows(RuntimeException.class,
                () -> itemService.deleteById(newItemDto.getId(), newUserDto.getId()));
    }

    @Test
    @DisplayName("Delete when user is owner")
    void testDeleteWhenUserIsOwner() {
        UserDto ownerDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto);
        itemService.deleteById(newItemDto.getId(), ownerDto.getId());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.getById(newItemDto.getId(), ownerDto.getId()));
    }

    @Test
    @DisplayName("Exception when delete item not exist")
    void shouldExceptionWhenDeleteItemNotExist() {
        UserDto ownerDto = userService.create(userDto1);
        assertThrows(ItemNotFoundException.class,
                () -> itemService.getById(-2L, ownerDto.getId()));
    }

    @Test
    @DisplayName("Return items by owner")
    void testReturnItemsByOwner() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(ownerDto.getId(), itemDto);
        itemService.create(ownerDto.getId(), itemDto1);

        List<ItemDto> listItems = itemService.getAll(ownerDto.getId(), 0, 10);

        assertEquals(2, listItems.size());
    }

    @Test
    @DisplayName("Return items by search")
    void shouldReturnItemsBySearch() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(ownerDto.getId(), itemDto);
        itemService.create(ownerDto.getId(), itemDto1);

        List<ItemDto> listItems = itemService.getItemsBySearch("itemDto", 0, 1);

        assertEquals(1, listItems.size());
    }

    @Test
    @DisplayName("Exception when create comment when user not booker")
    void testExceptionWhenCreateCommentWhenUserNotBooker() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(ownerDto.getId(), itemDto);

        CommentDto commentDto = CommentDto
                .builder()
                .id(1L)
                .text("Comment1")
                .itemId(itemDto.getId())
                .authorName(newUserDto.getName())
                .created(LocalDateTime.now())
                .build();

        assertThrows(CommentCreateException.class,
                () -> itemService.addComment(commentDto, newItemDto.getId(), newUserDto.getId()));
    }
}
