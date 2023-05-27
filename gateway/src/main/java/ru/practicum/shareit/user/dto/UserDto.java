package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя или логин пользователя отсутствует или передана пустая строка")
    private String name;

    @NotBlank(message = "Адрес электронной почты пользователя отсутствует или передана пустая строка")
    @Email(message = "Email пользователя указан некорректно")
    private String email;
}