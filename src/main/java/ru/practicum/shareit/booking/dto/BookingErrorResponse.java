package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingErrorResponse {
    private final String error;
    private final String description;

    public BookingErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
