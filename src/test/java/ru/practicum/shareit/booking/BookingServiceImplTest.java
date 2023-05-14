package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.booking.BookingNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    BookingRepository mockBookingRepository;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    ItemRepository itemRepository;

    @Test
    @DisplayName("Exception when get booking with wrong id")
    void testExceptionWhenGetBookingWithWrongId() {
        User user = User
                .builder()
                .id(1L)
                .name("name")
                .email("email@example.com")
                .build();

        BookingService bookingService = new BookingServiceImpl(mockBookingRepository,
                mockUserRepository, itemRepository);

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(-1L, 1L));
    }
}
