package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
public class UserServiceTest {

    private final UserService userService;

    private UserDto userDto;

    public UserServiceTest(@Autowired UserService userService) {
        this.userService = userService;
    }

    private final User user = User
            .builder()
            .id(1L)
            .name("UserTest")
            .email("userTest@mail.ru")
            .build();

    @BeforeEach
    void createDto() {
        userDto = userService.create(UserMapper.toUserDto(user));
    }

    @Test
    @DisplayName("Create user")
    void testCreateUser() {
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Get all users")
    void testGetAllUsers() {
        User user2 = User
                .builder()
                .id(2L)
                .name("UserTest2")
                .email("userTest2@mail.ru")
                .build();

        UserDto userDto2 = userService.create(UserMapper.toUserDto(user2));

        assertEquals(userService.getAll().size(), 2);
        assertThat(userService.getAll().get(0), equalTo(userDto));
        assertThat(userService.getAll().get(1), equalTo(userDto2));
    }

    @Test
    @DisplayName("Get user by id")
    void testGetUserById() {
        assertEquals(userService.getById(userDto.getId()), userDto);
    }

    @Test
    @DisplayName("Update user")
    void testUpdateUser() {
        userDto.setName("new user");
        userDto.setEmail("newUser@mail.ru");
        UserDto userDtoUpdated = userService.update(userDto);

        assertNotEquals(userDtoUpdated.getName(), "UserTest");
        assertThat(userDtoUpdated.getName(), equalTo("new user"));

        assertNotEquals(userDtoUpdated.getEmail(), "userTest@mail.ru");
        assertThat(userDtoUpdated.getEmail(), equalTo("newUser@mail.ru"));
    }

    @Test
    @DisplayName("Delete user by id")
    void testDeleteUserById() {
        int size = userService.getAll().size();

        assertEquals(size, 1);
        userService.deleteById(userDto.getId());
        assertNotEquals(size, userService.getAll().size());
    }
}
