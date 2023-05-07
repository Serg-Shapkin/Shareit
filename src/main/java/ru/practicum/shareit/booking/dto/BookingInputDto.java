package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingInputDto {
    private Long itemId;

    @NotNull(message = "Отсутствует дата и время начала бронирования")
    @Future(message = "Значение даты должно быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Отсутствует дата и время конца бронирования")
    @FutureOrPresent(message = "Значение даты должно быть в будущем (включая настоящее)")
    private LocalDateTime end;
}
