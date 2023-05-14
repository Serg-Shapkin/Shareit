package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> dtoJacksonTester;

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("UserTest")
            .email("userTest@mail.ru")
            .build();

    private final UserDto userDto1 = UserDto
            .builder()
            .id(1L)
            .name("UserTest")
            .email("userTest@mail.ru")
            .build();

    @Test
    @DisplayName("User DTO test")
    void testUserDto() throws IOException {
        JsonContent<UserDto> result = dtoJacksonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("UserTest");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("userTest@mail.ru");
    }

    @Test
    @DisplayName("User DTO equals test")
    void testUserEquals() {
        assertEquals(userDto, userDto);
        assertNotEquals(null, userDto);
        assertEquals(userDto, userDto1);
    }

    @Test
    @DisplayName("User DTO hash code test")
    void testUserHashCode() {
        assertEquals(Objects.hash(userDto.getId()), userDto.hashCode());
    }
}
