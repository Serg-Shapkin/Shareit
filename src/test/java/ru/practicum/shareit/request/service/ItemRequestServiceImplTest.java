package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.request.RequestNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    private ItemRequestService itemRequestService;

    private final User user = User
            .builder()
            .id(1L)
            .name("name")
            .email("email@example.com")
            .build();

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                userRepository, null);
    }

    @Test
    @DisplayName("Exception when get item request with wrong user id")
    void testExceptionWhenGetItemRequestWithWrongId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getById(-1L, 1L));
    }
}
