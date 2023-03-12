package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item create(Item item);
    List<Item> getAll(Long ownerId);
    Item getById(Long itemId);
    Item update(Item item, Long ownerId);
    List<Item> getItemsBySearch(String text);
}
