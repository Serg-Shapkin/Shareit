package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserErrorResponse {
    private final String error;
    private final String description;

    public UserErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
