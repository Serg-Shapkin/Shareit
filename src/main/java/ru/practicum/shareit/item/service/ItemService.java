package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto itemDto);

    List<ItemDto> getAll(Long ownerId);

    ItemDto getById(Long itemId, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    void deleteById(Long itemId, Long ownerId);

    List<ItemDto> getItemsBySearch(String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
