package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"/schema.sql"})
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;


    User user = User
            .builder()
            .name("UserName")
            .email("user@email.com")
            .build();

    User booker = User
            .builder()
            .name("BookerName")
            .email("booker@email.com")
            .build();

    Item item = Item
            .builder()
            .name("ItemName")
            .description("itemDescription")
            .ownerId(user.getId())
            .available(true)
            .build();

    Booking booking = Booking
            .builder()
            .start(LocalDateTime.of(2025, 1, 1, 12, 0))
            .end(LocalDateTime.of(2025, 1, 1, 13, 0))
            .item(item)
            .booker(booker)
            .status(Status.WAITING)
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();

        user.setId(null);
        user = userRepository.save(user);

        booker.setId(null);
        booker = userRepository.save(booker);

        item.setId(null);
        item.setOwnerId(user.getId());
        item = itemRepository.save(item);

        booking.setId(null);
        booking.setItem(item);
    }

    @Test
    @DisplayName("Save booking")
    void testSaveBooking() {
        Booking saveBooking = bookingRepository.save(booking);

        assertEquals(1, saveBooking.getId());
        assertEquals(item.getId(), saveBooking.getItem().getId());
        assertEquals(booker.getId(), saveBooking.getBooker().getId());
    }

    @Test
    @DisplayName("Find by booker id")
    void testFindByBookerId() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerId(booker.getId(), PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by booker id and start is before and end is after")
    void testFindByBookerIdAndStartIsBeforeAndEndIsAfter() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                booker.getId(),
                LocalDateTime.of(2025, 1, 1, 15, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by booker id and start is before and end is before")
    void findByBookerIdAndStartIsBeforeAndEndIsBefore() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                booker.getId(),
                LocalDateTime.of(2025, 1, 1, 15, 0),
                LocalDateTime.of(2025, 1, 1, 18, 0),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by booker id and start is after")
    void findByBookerIdAndStartIsAfter() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsAfter(
                booker.getId(),
                LocalDateTime.of(2022, 1, 1, 15, 0),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by booker id and status")
    void findByBookerIdAndStatus() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(
                booker.getId(),
                Status.WAITING,
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by item owner id")
    void findByItem_OwnerId() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItem_OwnerId(
                user.getId(),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by item owner id and start is before and end is after")
    void findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(
                user.getId(),
                LocalDateTime.of(2025, 1, 1, 15, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by item owner id and end is before")
    void findByItem_OwnerIdAndEndIsBefore() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndEndIsBefore(
                user.getId(),
                LocalDateTime.of(2025, 1, 1, 15, 0),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by item owner id and start is after")
    void findByItem_OwnerIdAndStartIsAfter() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndStartIsAfter(
                user.getId(),
                LocalDateTime.of(2022, 1, 1, 15, 0),
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    @DisplayName("Find by item owner id and status")
    void findByItem_OwnerIdAndStatus() {
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findByItem_OwnerIdAndStatus(
                user.getId(),
                Status.WAITING,
                PageRequest.of(0, 10)
        );

        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals(item.getId(), bookings.get(0).getItem().getId());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }
}
