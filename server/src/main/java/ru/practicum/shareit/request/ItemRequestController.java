package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /requests", "POST");
        return itemRequestService.create(itemRequestDto, userId, LocalDateTime.now());
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /requests/{}", "GET", userId);
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                       @RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /requests/all/?from={}&size={}", "GET", from, size);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ItemRequestDto getById(@PathVariable(value = "requestId") Long requestId,
                                  @RequestHeader(HEADER) Long userId) {
        log.info("Получен запрос к эндпоинту:{} /requests/{}", "GET", requestId);
        return itemRequestService.getById(requestId, userId);
    }
}
