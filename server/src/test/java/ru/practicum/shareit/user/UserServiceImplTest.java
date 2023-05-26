package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImp;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("UserTest")
            .email("userTest@mail.ru")
            .build();

    private final User user = User
            .builder()
            .id(1L)
            .name("UserTest")
            .email("userTest@mail.ru")
            .build();

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImp(userRepository);
    }

    @Test
    @DisplayName("Create user")
    void testCreateUser() {
        when(userRepository.save(any()))
                .thenReturn(user);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        userService.create(userDto);

        assertEquals(userService.getById(user.getId()), userDto);
    }

    @Test
    @DisplayName("Get all users")
    void testGetAllUsers() {
        when(userRepository.save(any()))
                .thenReturn(user);

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        userService.create(userDto);

        List<UserDto> userDtos = userService.getAll();

        assertEquals(userDtos.size(), 1);
        assertEquals(userDtos.get(0).getId(), user.getId());
        assertEquals(userDtos.get(0).getName(), user.getName());
        assertEquals(userDtos.get(0).getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Get all users empty list")
    void shouldReturnEmptyUsersList() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        assertEquals(userService.getAll().size(), 0);
    }

    @Test
    @DisplayName("Get user by id")
    void testGetUserById() {
        when(userRepository.save(any()))
                .thenReturn(user);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        userService.create(userDto);

        UserDto userDto = userService.getById(user.getId());
        assertEquals(userDto.getId(), userDto.getId());
        assertEquals(userDto.getName(), userDto.getName());
        assertEquals(userDto.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("Exception when get user by wrong id")
    void shouldExceptionWhenGetUserByWrongId() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getById(2L));
    }

    @Test
    @DisplayName("Update user by name")
    void testUpdateUserByName() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenReturn(user);

        assertThat(userService.create(userDto).getName(), equalTo("UserTest"));

        user.setName("new name");
        userDto.setName("new name");

        assertThat(userService.update(userDto).getName(), equalTo("new name"));
    }

    @Test
    @DisplayName("Update user by email")
    void testUpdateUserByEmail() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenReturn(user);

        assertThat(userService.create(userDto).getEmail(), equalTo("userTest@mail.ru"));

        user.setEmail("newEmail@mail.ru");
        userDto.setEmail("newEmail@mail.ru");

        assertThat(userService.update(userDto).getEmail(), equalTo("newEmail@mail.ru"));
    }

    @Test
    @DisplayName("Exception when update not exist user")
    void shouldExceptionWhenUpdateNotExistUser() {
        userDto.setId(null);
        assertThrows(UserNotFoundException.class, () -> userService.update(userDto));
    }

    @Test
    @DisplayName("Exception when update user if wrong id")
    void shouldExceptionWhenUpdateUserIfWrongId() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.update(userDto));
    }

    @Test
    @DisplayName("Delete user by id")
    void shouldDeleteUserById() {
        assertDoesNotThrow(() -> userService.deleteById(userDto.getId()));
    }
}
