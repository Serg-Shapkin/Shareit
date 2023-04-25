package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;

    @NotBlank(message = "Описание запроса вещи отсутствует или передана пустая строка")
    @Size(max = 200)
    private String description;

    @NotNull(message = "Отсутствует пользователь создавший запрос")
    private User requestor;

    @NotNull(message = "Отсутствует дата и время создания запроса")
    private LocalDateTime created;
}
