package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemErrorResponse {
    private final String error;
    private final String description;

    public ItemErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
