package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotDescriptionException;
import ru.practicum.shareit.item.exception.ItemNotNameException;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody ItemDto itemDto) {
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
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен запрос к эндпоинту:{} {}", "/items", "GET");
        return itemService.getAll(ownerId);
    }

    @GetMapping(value = "/{id}")
    public ItemDto getById(@PathVariable(value = "id") Long id) {
        log.info("Получен запрос к эндпоинту:{} /items/{}", "GET", id);
        return itemService.getById(id);
    }

    @PatchMapping(value = "/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable(value = "id") Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту:{} /items/{}", "PATCH", id);
        return itemService.update(itemDto, id, ownerId);
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
}
