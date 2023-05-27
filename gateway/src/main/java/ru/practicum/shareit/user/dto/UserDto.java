package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = {ru.practicum.shareit.validation.Create.class})
    private String name;
    @Email(groups = {Update.class, ru.practicum.shareit.validation.Create.class})
    @NotNull(groups = {ru.practicum.shareit.validation.Create.class})
    @NotBlank
    @NotEmpty(groups = {Create.class})
    private String email;
}