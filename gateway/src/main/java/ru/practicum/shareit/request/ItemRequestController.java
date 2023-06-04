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
    public ResponseEntity<Object> create(@RequestHeader(HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Post userId={}, itemRequestDto={}", userId, itemRequestDto);
        return requestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get /requestId userdId={}, requestId={}", userId, requestId);
        return requestClient.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequest(
            @RequestHeader(HEADER) Long userId) {
        log.info("Get allUserRequest userId={}", userId);
        return requestClient.getAllUserRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get /all userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequest(userId, from, size);
    }
}
