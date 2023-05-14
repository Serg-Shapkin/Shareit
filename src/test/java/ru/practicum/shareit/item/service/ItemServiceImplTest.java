package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    private final User user = User
            .builder()
            .id(1L)
            .name("name")
            .email("email@example.com")
            .build();

    private final Item item = Item
            .builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .ownerId(user.getId())
            .available(true)
            .build();

    private final ItemDto itemDto = ItemDto
            .builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .ownerId(user.getId())
            .available(true)
            .nextBooking(null)
            .lastBooking(null)
            .comments(List.of())
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(user)
            .item(item)
            .build();

    public static final Integer FROM = 0;
    public static final Integer SIZE = 10;


    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    @DisplayName("Return empty items list")
    void testReturnEmptyItemsList() {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(FROM / SIZE, SIZE, sort);

        when(itemRepository.findByOwnerId(user.getId(), pageRequest))
                .thenReturn(List.of());
        assertEquals(itemService.getAll(user.getId(), FROM, SIZE).size(), 0);
    }

    @Test
    @DisplayName("Return all items")
    void testReturnAllItems() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(FROM / SIZE, SIZE, sort);

        when(itemRepository.findByOwnerId(user.getId(), pageRequest))
                .thenReturn(List.of(item));

        assertEquals(1, itemService.getAll(user.getId(), FROM, SIZE).size());

        when(bookingRepository.findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(booking);

        assertEquals(1, itemService.getAll(user.getId(), FROM, SIZE).size());
    }

    @Test
    @DisplayName("Get item by id")
    void testGetItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertEquals(itemDto, itemService.getById(user.getId(), item.getId()));

        item.setOwnerId(2L);
        itemDto.setOwnerId(2L);
        assertEquals(itemDto, itemService.getById(user.getId(), item.getId()));
    }

    @Test
    @DisplayName("Exception when get item by id")
    void testExceptionWhenGetItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getById(user.getId(), item.getId()));
    }

    @Test
    @DisplayName("Return empty list of item by searching blank text")
    void testReturnEmptyListOfItemsBySearchingBlankText() {
        assertEquals(List.of(), itemService.getItemsBySearch("   ", FROM, SIZE));
    }
}
