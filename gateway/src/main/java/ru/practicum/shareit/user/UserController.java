package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "id") long userId) {
        log.info("Get user by userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "id") long userId,
                                             @RequestBody UserDto userDto) {
        log.info("Update user {}, userId={}", userDto, userId);
        userDto.setId(userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable(value = "id") Long userId) {
        log.info("Delete user by userId={}", userId);
        userClient.deleteUserById(userId);
    }
}
