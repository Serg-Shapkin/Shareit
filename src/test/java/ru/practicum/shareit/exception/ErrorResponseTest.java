package ru.practicum.shareit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorResponseTest {

    ErrorResponse errorResponse = new ErrorResponse("error", "message");

    ErrorResponse errorResponse2 = new ErrorResponse("error", "message");

    @Test
    @DisplayName("Error response equals test")
    void testErrorResponseEquals() {
        assertEquals(errorResponse, errorResponse);
        assertNotEquals(null, errorResponse);
        assertEquals(errorResponse, errorResponse2);
    }

    @Test
    @DisplayName("Error response hash code test")
    void testErrorResponseHashCode() {
        assertEquals(Objects.hash(errorResponse.getError(), errorResponse.getDescription()), errorResponse.hashCode());
    }

    @Test
    @DisplayName("Error get description and error test")
    public void testErrorGetDescriptionAndError() {
        assertEquals("error", errorResponse.getError());
        assertEquals("message", errorResponse.getDescription());
    }
}
