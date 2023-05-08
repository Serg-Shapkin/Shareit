package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserCreateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.toUser(userDto));
            log.info("Создан пользователь: {}", user);
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("Невозможно создать пользователя");
            throw new UserCreateException();
        }
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Запрошен список всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Запрошен пользователь с id: {}", id);
        User user = getUser(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        Long userId = user.getId();

        if (userId == null) {
            log.debug("Пользователь с id: {} не найден", userId);
            throw new UserNotFoundException(userId);
        }

        User userToUpdate = getUser(userId);

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        userRepository.save(userToUpdate);
        log.info("Пользователь: {} обновлен", user);
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        log.info("Пользователь с id: {} удален", id);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}
