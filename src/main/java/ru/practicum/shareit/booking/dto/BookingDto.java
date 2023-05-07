package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Отсутствует дата и время начала бронирования")
    @Future(message = "Значение даты должно быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Отсутствует дата и время конца бронирования")
    @FutureOrPresent(message = "Значение даты должно быть в будущем (включая настоящее)")
    private LocalDateTime end;

    @NotNull(message = "Отсутствует вещь, которую бронирует пользователь")
    private ItemDto item;

    @NotNull(message = "Отсутствует пользователь, который осуществляет бронирование")
    private UserDto booker;

    @NotNull(message = "Отсутствует статус бронирования")
    private Status status;
}
