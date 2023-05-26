package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader(HEADER) long userId) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequest(@RequestHeader(HEADER) long userId) {
        log.info("Get all user requests, userId={}", userId);
        return requestClient.getAllUserRequests(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAll(@PositiveOrZero
                                           @RequestParam(
                                               name = "from",
                                               defaultValue = "0") int from,
                                       @Positive
                                            @RequestParam(
                                               name = "size",
                                               required = false,
                                               defaultValue = "10") int size,
                                       @RequestHeader(HEADER) Long userId) {
        log.info("Get all requests userId={}, from={} size={}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable(value = "requestId") long requestId,
                                          @RequestHeader(HEADER) long userId) {
        log.info("Get request by requestId={}, userId={}", requestId, userId);
        return requestClient.getRequestById(requestId, userId);
    }
}
