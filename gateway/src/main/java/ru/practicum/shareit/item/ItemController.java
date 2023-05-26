package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER) long ownerId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item ownerId={}, itemDto={}", ownerId, itemDto);
        return itemClient.createItem(ownerId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER) long ownerId,
                                         @PositiveOrZero
                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                         @Positive
                                             @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        log.info("Get all items by ownerId={}, from={}, size={}", ownerId, from, size);
        return itemClient.getAllItemsByOwner(ownerId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable(value = "itemId") long itemId,
                                          @RequestHeader(HEADER) Long userId) {
        log.info("Get item by id, itemId={}, userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER) long ownerId,
                                         @PathVariable(value = "itemId") long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Update item {}, itemId={}, ownerId={}", itemDto, itemId, ownerId);
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam(value = "text", defaultValue = "") String text,
                                                   @PositiveOrZero
                                                   @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @Positive
                                                       @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        log.info("Get items bi search, text={}, from={}, size={}", text, from, size);
        if (text.isEmpty()) {
            log.info("An empty request was passed");
        }
        return itemClient.getItemsBySearch(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable(value = "itemId") Long itemId,
                                             @RequestHeader(HEADER) Long authorId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Add comment {}, itemId={}, authorId={}", commentDto, itemId, authorId);
        return itemClient.addComment(commentDto, itemId, authorId);
    }
}
