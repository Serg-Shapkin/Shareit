package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.user.UserCreateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.user.UserNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final UserDto userDto = UserDto
            .builder()
            .id(1L)
            .name("UserTest")
            .email("userTest@mail.ru")
            .build();

    @Test
    @DisplayName("Create user")
    void testCreateUser() throws Exception {

        when(userService.create(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    @DisplayName("Get all user")
    void testGetAll() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .header(HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName()), String.class));
    }

    @Test
    @DisplayName("Get user by id")
    void testGetById() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .header(HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    @DisplayName("Update user")
    void testUpdateUser() throws Exception {
        when(userService.update(any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .header(HEADER, 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class));
    }

    @Test
    @DisplayName("Delete user")
    public void testRemoveUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .header(HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

/*    @Test
    @DisplayName("Validation Exception")
    public void testHandleValidationException() throws Exception {
        UserDto userDto1 = UserDto
                .builder()
                .id(1L)
                .email("ValidationException")
                .name("name")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных"), String.class));
    }*/

    @Test
    @DisplayName("User not found Exception")
    public void testHandleUserNotFoundException() throws Exception {
        Mockito.when(userService.create(Mockito.any()))
                .thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("User create Exception")
    public void testHandleUserCreateException() throws Exception {
        Mockito.when(userService.create(Mockito.any()))
                .thenThrow(UserCreateException.class);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Ошибка при создании пользователя"), String.class));
    }

/*    @Test
    @DisplayName("Argument not valid Exception")
    public void testHandleMethodArgumentNotValidException() throws Exception {
        UserDto invalidDto = UserDto
                .builder()
                .id(null)
                .email(null)
                .name(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации данных"), String.class));
    }*/
}
