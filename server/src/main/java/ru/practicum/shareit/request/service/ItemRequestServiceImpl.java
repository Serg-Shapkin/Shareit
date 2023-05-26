package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.exception.request.RequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(ItemRequestDto requestDto, Long userId, LocalDateTime created) {
        getUser(userId);
        ItemRequest itemRequest = RequestMapper.toItemRequest(requestDto);
        itemRequest.setId(userId);
        itemRequest.setRequestor(getUser(userId));
        itemRequest.setCreated(created);
        itemRequestRepository.save(itemRequest);
        log.info("Создан запрос: {}", itemRequest);
        return RequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        getUser(userId);

        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findByRequestorId(
                userId,
                Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        setItemsForRequest(itemRequestDtoList);
        log.info("Запрошен список всех запросов пользователя");
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        getUser(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findAllByRequestorIdNot(
                userId,
                pageRequest)
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        setItemsForRequest(itemRequestDtoList);
        log.info("Запрошен список всех запросов");
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        getUser(userId);
        ItemRequest itemRequest = getRequest(requestId);

        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(
                itemRepository.findByRequestId(requestId)
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
        );
        log.info("Запрошен запрос с id: {}", requestId);
        return itemRequestDto;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
    }

    private void setItemsForRequest(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList.forEach(
                (item) -> item.setItems(
                        itemRepository.findByRequestId(item.getId())
                                .stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())
                )
        );
    }
}
