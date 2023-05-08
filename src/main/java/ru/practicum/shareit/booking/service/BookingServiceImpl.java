package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BookingCreateException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(BookingInputDto bookingInputDto, Long bookerId) {
        User user = getUser(bookerId);
        Item item = getItem(bookingInputDto.getItemId());

        if ((item.getAvailable().equals(false))) {
            throw new BookingCreateException("Выбранная вещь недоступна для бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingInputDto, user, item);
        booking.setStatus(Status.WAITING);

        if (bookerId.equals(booking.getItem().getOwnerId())) {
            throw new InvalidBookingException("Невозможно забронировать собственную вещь");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingCreateException("Дата окончания бронирования не может быть раньше начала");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new BookingCreateException("Время начала и окончания бронирования не должны совпадать");
        }
        log.info("Создан запрос на бронирование вещи");
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        getUser(userId);
        Booking booking = getBooking(bookingId);

        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setStatus(Status.CANCELED);
            } else {
                throw new InvalidBookingException("Пользователь не может подтвердить бронирование");
            }
        } else if (booking.getItem().getOwnerId().equals(userId) && (!booking.getStatus().equals(Status.CANCELED))) {
            if (!booking.getStatus().equals(Status.WAITING)) {
                throw new BookingCreateException("Решение по бронированию уже есть");
            }
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
        } else {
            if (booking.getStatus().equals(Status.CANCELED)) {
                throw new InvalidBookingException("Бронирование отменено!");
            } else {
                throw new InvalidBookingException("Подтвердить бронирование может только владелец");
            }
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        getUser(userId);
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            log.info("Запрошено бронирование: {}", booking);
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Информация о бронировании доступна только автору или владельцу");
        }
    }

    @Override
    public List<BookingDto> getBookings(String state, Long userId, Integer from, Integer size) {
        getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Booking> bookings;
        switch (State.valueOf(state.toUpperCase())) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(
                        userId,
                        LocalDateTime.now(),
                        pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(
                        userId,
                        Status.WAITING,
                        pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(
                        userId,
                        Status.REJECTED,
                        pageRequest);
                break;
            default:
                log.warn("Передан некорректный параметр: {}", state);
                throw new UnsupportedStatusException(state);
        }
        log.info("Запрошен список всех бронирований текущего пользователя");
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(String state, Long ownerId, Integer from, Integer size) {
        getUser(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<Booking> bookings;
        switch (State.valueOf(state.toUpperCase())) {
            case ALL:
                bookings = bookingRepository.findByItem_OwnerId(ownerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(
                        ownerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_OwnerIdAndEndIsBefore(
                        ownerId,
                        LocalDateTime.now(),
                        pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_OwnerIdAndStartIsAfter(
                        ownerId,
                        LocalDateTime.now(),
                        pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_OwnerIdAndStatus(
                        ownerId,
                        Status.WAITING,
                        pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_OwnerIdAndStatus(
                        ownerId,
                        Status.REJECTED,
                        pageRequest);
                break;
            default:
                log.warn("Передан некорректный параметр: {}", state);
                throw new UnsupportedStatusException(state);
        }
        log.info("Запрошен список бронирований для всех вещей текущего пользователя");
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}
