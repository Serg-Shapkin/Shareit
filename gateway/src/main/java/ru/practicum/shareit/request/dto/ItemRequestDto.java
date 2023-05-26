package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
}
