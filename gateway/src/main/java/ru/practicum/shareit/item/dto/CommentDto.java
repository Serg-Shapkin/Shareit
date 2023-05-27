package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String text;
    private ItemDto item;
    private String authorName;
}
