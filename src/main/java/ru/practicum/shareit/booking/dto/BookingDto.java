package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "Отсутствует дата и время начала бронирования")
    private LocalDateTime start;

    @NotNull(message = "Отсутствует дата и время конца бронирования")
    private LocalDateTime end;

    @NotNull(message = "Отсутствует вещь, которую бронирует пользователь")
    private Item item;

    @NotNull(message = "Отсутствует пользователь, который осуществляет бронирование")
    private User booker;

    @NotNull(message = "Отсутствует статус бронирования")
    private Status status;
}