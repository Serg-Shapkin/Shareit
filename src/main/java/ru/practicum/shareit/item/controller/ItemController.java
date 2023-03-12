package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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
        return itemService.getItemsBySearch(text);
    }
}
