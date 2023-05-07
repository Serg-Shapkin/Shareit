package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту:{} /users", "POST");
        return userService.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос к эндпоинту:{} {}", "/users", "GET");
        return userService.getAll();
    }

    @GetMapping(value = "/{id}")
    public UserDto getById(@PathVariable(value = "id") Long id) {
        log.info("Получен запрос к эндпоинту:{} /users/{}", "GET", id);
        return userService.getById(id);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@PathVariable(value = "id") Long id,
                          @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту:{} /users/{}", "PATCH", id);
        userDto.setId(id);
        return userService.update(userDto);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable(value = "id") Long id) {
        log.info("Получен запрос к эндпоинту:{} /users/{}", "DELETE", id);
        userService.deleteById(id);
    }
}
