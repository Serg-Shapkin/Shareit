package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull(message = "Отсутствует itemId")
	private long itemId;

	@NotNull(message = "Отсутствует дата и время начала бронирования")
	@FutureOrPresent(message = "Значение даты должно быть в будущем (включая настоящее)")
	private LocalDateTime start;

	@NotNull(message = "Отсутствует дата и время начала бронирования")
	@Future(message = "Значение даты должно быть в будущем")
	private LocalDateTime end;
}
