package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotDescriptionException;
import ru.practicum.shareit.item.exception.ItemNotNameException;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER) Long ownerId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту:{} /items", "POST");

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemNotNameException("Отсутствует название вещи");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemNotDescriptionException("Отсутствует описание вещи");
        }
        if (itemDto.getAvailable() == null) {
            throw new ItemNotAvailableException("Отсутствует статус доступности вещи");
        }

        itemDto.setId(ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(HEADER) Long ownerId) {
        log.info("Получен запрос к эндпоинту:{} {}", "/items", "GET");
        return itemService.getAll(ownerId);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getById(@PathVariable(value = "itemId") Long itemId,
                           @RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /items/{}", "GET", itemId);
        return itemService.getById(itemId, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto update(@RequestHeader(HEADER) Long ownerId,
                          @PathVariable(value = "itemId") Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту:{} /items/{}", "PATCH", itemId);
        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("Получен запрос к эндпоинту:{} /items/search?text={}", "GET", text);
        if (text.isEmpty()) {
            log.info("Передан пустой запрос");
            return new ArrayList<>();
        }
        return itemService.getItemsBySearch(text);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto addComment(@PathVariable(value = "itemId") Long itemId,
                                 @RequestHeader(HEADER) Long authorId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту:{} {}", String.format("%d/comment", itemId), "POST");
        return itemService.addComment(commentDto, itemId, authorId);
    }
}
