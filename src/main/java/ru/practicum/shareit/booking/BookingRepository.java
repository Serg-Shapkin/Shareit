package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsBefore(Long bookerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId,
                                                LocalDateTime start,
                                                Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId,
                                          Status status,
                                          Sort sort);

    List<Booking> findByItem_OwnerId(Long ownerId, Sort sort);

    List<Booking> findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                  LocalDateTime start,
                                                                  LocalDateTime end,
                                                                  Sort sort);

    List<Booking> findByItem_OwnerIdAndEndIsBefore(Long ownerId,
                                                   LocalDateTime end,
                                                   Sort sort);

    List<Booking> findByItem_OwnerIdAndStartIsAfter(Long ownerId,
                                                     LocalDateTime start,
                                                     Sort sort);

    List<Booking> findByItem_OwnerIdAndStatus(Long ownerId,
                                              Status status,
                                              Sort sort);

    Booking findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime end);

    Booking findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime start);

    Booking findFirstByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);
}
