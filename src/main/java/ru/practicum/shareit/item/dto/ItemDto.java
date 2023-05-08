package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название вещи отсутствует или передана пустая строка")
    private String name;

    @NotEmpty(message = "Описание вещи отсутствует")
    private String description;

    @NotNull(message = "Статус доступности вещи отсутствует")
    private Boolean available;

    @NotNull(message = "Владелец вещи отсутствует")
    private Long ownerId;

    private Long requestId;

    private List<CommentDto> comments;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}
