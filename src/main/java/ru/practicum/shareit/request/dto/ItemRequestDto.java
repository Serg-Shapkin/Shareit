package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса вещи отсутствует или передана пустая строка")
    @Size(max = 200)
    private String description;

    private User requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}
