package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
public class CommentDto {

    private Long id;

    @NotBlank(message = "Текст комментария отсутствует или передана пустая строка")
    private String text;

    private Long ItemId;

    private String authorName;

    private LocalDateTime created;
}
