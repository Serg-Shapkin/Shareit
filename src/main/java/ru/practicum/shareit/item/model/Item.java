package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@Builder
public class Item {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует или передана пустая строка")
    private String name;

    @NotEmpty(message = "Описание вещи отсутствует")
    @Size(max = 200)
    private String description;

    @NotNull(message = "Статус доступности вещи отсутствует")
    private Boolean available;
    @NotNull(message = "Владелец вещи отсутствует")
    private User owner;

    private ItemRequest request;
}
