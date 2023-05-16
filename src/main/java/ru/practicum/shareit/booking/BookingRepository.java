package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsBefore(Long bookerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId,
                                                LocalDateTime start,
                                                Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId,
                                          Status status,
                                          Pageable pageable);

    List<Booking> findByItem_OwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                  LocalDateTime start,
                                                                  LocalDateTime end,
                                                                  Pageable pageable);

    List<Booking> findByItem_OwnerIdAndEndIsBefore(Long ownerId,
                                                   LocalDateTime end,
                                                   Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStartIsAfter(Long ownerId,
                                                    LocalDateTime start,
                                                    Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStatus(Long ownerId,
                                              Status status,
                                              Pageable pageable);

    Booking findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime end);

    Booking findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime start);

    Booking findFirstByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);
}
