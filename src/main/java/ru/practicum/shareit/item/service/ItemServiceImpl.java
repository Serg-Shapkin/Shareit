package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.comment.CommentCreateException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = getUser(ownerId);
        itemDto.setOwnerId(owner.getId());
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        log.info("Создана вещь: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long ownerId, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        List<ItemDto> items = itemRepository.findByOwnerId(ownerId, pageRequest)
                .stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());

        items.forEach(this::setLastAndNextBooking);

        items.forEach(itemDto -> itemDto.setComments(commentRepository.findAllByItem_Id(itemDto.getId(),
                        Sort.by(Sort.Direction.DESC, "created"))
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList())));

        log.info("Запрошен список всех вещей владельца");
        return items;
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = getItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (userId.equals(item.getOwnerId())) {
            setLastAndNextBooking(itemDto);
        }

        itemDto.setComments(commentRepository.findAllByItem_Id(itemId,
                Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        log.info("Запрошена вещь с id: {}", itemId);
        return itemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        Item itemToUpdate = getItem(itemId);

        if (!itemToUpdate.getOwnerId().equals(ownerId)) {
            log.warn("Вещь с id: {} не найдена", itemId);
            throw new ItemNotFoundException(itemId);
        }
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(itemToUpdate);
        log.info("Вещь: {} обновлена", itemToUpdate);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Override
    public void deleteById(Long itemId, Long ownerId) {
        Item item = getItem(itemId);
        if (item.getOwnerId().equals(ownerId)) {
            itemRepository.deleteById(itemId);
            log.info("Вещь: {} удалена", item);
        } else {
            log.warn("Удаление вещи: \"{}\" невозможно", item);
            throw new RuntimeException(String.format("Удаление вещи: \"%s\" невозможно", item));
        }
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            log.info("Поиск вещи по слову: \"{}\"", text);
            return itemRepository.getItemsBySearch(text, pageRequest)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            log.warn("Поиск вещи по слову: \"{}\" не дал результатов", text);
            return new ArrayList<>();
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = getUser(userId);
        Item item = getItem(itemId);

        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndBefore(
                itemId, userId, LocalDateTime.now());

        if (booking != null) {
            Comment comment = CommentMapper.toComment(commentDto, user, item);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            log.warn("Пользователь не бронировал вещь, поэтому не может оставить комментарий");
            throw new CommentCreateException("Пользователь не бронировал вещь, поэтому не может оставить комментарий");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private void setLastAndNextBooking(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(
                itemDto.getId(), Status.APPROVED, LocalDateTime.now());

        Booking nextBooking = bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                itemDto.getId(), Status.APPROVED, LocalDateTime.now());

        if (lastBooking == null) {
            itemDto.setLastBooking(null);
        } else {
            itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        }

        if (nextBooking == null) {
            itemDto.setNextBooking(null);
        } else {
            itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
        }
    }
}
