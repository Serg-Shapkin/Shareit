package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = userDao.getById(ownerId);
        Item item = itemDao.create(ItemMapper.toItem(itemDto));
        item.setOwner(owner);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long ownerId) {
        return itemDao.getAll(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemDao.getById(id);
        if (item != null) {
            return ItemMapper.toItemDto(item);
        } else {
            throw new ItemNotFoundException(id);
        }
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        itemDto.setId(itemId);
        Item itemToUpdate = itemDao.update(ItemMapper.toItem(itemDto), ownerId);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        return itemDao.getItemsBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
