package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса вещи отсутствует или передана пустая строка")
    @Size(max = 200)
    private String description;

    @NotNull(message = "Отсутствует пользователь создавший запрос")
    private User requestor;

    @NotNull(message = "Отсутствует дата и время создания запроса")
    private LocalDateTime created;
}
