package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Item item) {
        id++;
        item.setId(id);
        items.put(item.getId(), item);
        log.info("Создана вещь:{}", item);
        return item;
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        log.info("Запрошен список всех вещей владельца");
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long itemId) {
        log.info("Запрошена вещь с id:{}", itemId);
        return items.get(itemId);
    }

    @Override
    public Item update(Item item, Long ownerId) {

        Item itemToUpdate = items.get(item.getId());
        if (!itemToUpdate.getOwner().getId().equals(ownerId)) {
            throw new ItemNotFoundException(item.getId());
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        log.info("Вещь:{} обновлена", item);
        return itemToUpdate;
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        log.info("Запрошен поиск вещи по тексту:{}", text);
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                || item.getDescription().toLowerCase().contains(text))
                && item.getAvailable())
                .collect(Collectors.toList());
    }
}
