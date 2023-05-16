package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserMapperTest {

    private final User user = User
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("User")
            .email("user@mail.ru")
            .build();

    @Test
    @DisplayName("User to UserDTO")
    void testUserToUserDto() {

        UserDto userDto1 = UserMapper.toUserDto(user);

        assertEquals(userDto1, userDto);
        assertEquals(userDto1.getId(), userDto.getId());
        assertEquals(userDto1.getName(), userDto.getName());
        assertEquals(userDto1.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("UserDTO to User")
    void testUserDtoToUser() {

        User user1 = UserMapper.toUser(userDto);

        assertEquals(user1, user);
        assertEquals(user1.getId(), user.getId());
        assertEquals(user1.getName(), user.getName());
        assertEquals(user1.getEmail(), user.getEmail());
    }
}
