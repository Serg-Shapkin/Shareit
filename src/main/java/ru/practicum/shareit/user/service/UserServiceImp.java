package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userDao.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userDao.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userDao.getById(id);
        if (user != null) {
            return UserMapper.toUserDto(user);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public UserDto update(UserDto userDto) {
        User userToUpdate = userDao.update(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }
}
