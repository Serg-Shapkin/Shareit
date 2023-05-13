package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private final User user = User
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    private final User user2 = User
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    @Test
    @DisplayName("User equals test")
    void userEqualsTest() {
        assertEquals(user, user);
        assertNotEquals(null, user);
        assertEquals(user, user2);
    }

    @Test
    @DisplayName("User hash code test")
    void userHashCodeTest() {
        assertEquals(Objects.hash(user.getId()), user.hashCode());
    }
}
