package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.item.ItemNotAvailableException;
import ru.practicum.shareit.exception.item.ItemNotDescriptionException;
import ru.practicum.shareit.exception.item.ItemNotNameException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {ItemController.class})
public class ItemControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";

    private final List<ItemDto> listItemDto = new ArrayList<>();

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final UserDto userOwnerDto = UserDto
            .builder()
            .id(1L)
            .name("OwnerName")
            .email("owner@mail.ru")
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("Description")
            .available(true)
            .ownerId(userOwnerDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final ItemDto itemDtoWithoutName = ItemDto
            .builder()
            .id(1L)
            .name("")
            .description("Description")
            .available(true)
            .ownerId(userOwnerDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final ItemDto itemDtoWithoutDescription = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("")
            .available(true)
            .ownerId(userOwnerDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final ItemDto itemDtoWithoutAvailable = ItemDto
            .builder()
            .id(1L)
            .name("ItemName")
            .description("Description")
            .available(null)
            .ownerId(userOwnerDto.getId())
            .requestId(null)
            .comments(null)
            .lastBooking(null)
            .nextBooking(null)
            .build();

    private final CommentDto commentDto = CommentDto
            .builder()
            .id(1L)
            .text("text")
            .itemId(itemDto.getId())
            .authorName(userOwnerDto.getName())
            .created(LocalDateTime.of(2025, 01, 01, 12, 00))
            .build();

    @Test
    @DisplayName("Create item")
    void testCreateItem() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    @DisplayName("Create item without name")
    void testCreateItemWithoutName() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenThrow(ItemNotNameException.class);

        mockMvc.perform(post("/items")
                .header(HEADER, 1)
                .content(objectMapper.writeValueAsString(itemDtoWithoutName))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка в названии вещи"), String.class));
    }

    @Test
    @DisplayName("Create item without description")
    void testCreateItemWithoutDescription() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenThrow(ItemNotDescriptionException.class);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDtoWithoutDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка в описании вещи"), String.class));
    }

    @Test
    @DisplayName("Create item without available")
    void testCreateItemWithoutAvailable() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenThrow(ItemNotAvailableException.class);

        mockMvc.perform(post("/items")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDtoWithoutAvailable))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка статуса доступности вещи"), String.class));
    }

    @Test
    @DisplayName("Get all items by owner")
    void testGetAll() throws Exception {
        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(listItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    @DisplayName("Get item by id")
    void testGetById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    @DisplayName("Update item")
    void testUpdateItem() throws Exception {
        when(itemService.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                .header(HEADER, 1)
                .content(objectMapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    @DisplayName("Get items by search")
    void testGetItemsBySearch() throws Exception {
        when(itemService.getItemsBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(listItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    @DisplayName("Add comment")
    void testAddComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }



}
