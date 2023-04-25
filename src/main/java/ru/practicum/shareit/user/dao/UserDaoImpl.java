package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.UserDuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        checkEmail(user);
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Создан пользователь:{}", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Запрошен список всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        log.info("Запрошен пользователь с id:{}", id);
        if (!users.containsKey(id)) {
            log.debug("Пользователь с id:{} не найден", id);
            throw new UserNotFoundException(id);
        }
        return users.get(id);
    }

    @Override
    public User update(User user) {
        Long userId = user.getId();
        if (!users.containsKey(userId)) {
            log.debug("Пользователь с id:{} не найден", userId);
            throw new UserNotFoundException(userId);
        }
        User userToUpdate = users.get(userId);

        if (!userToUpdate.getEmail().equals(user.getEmail())) {
            checkEmail(user);
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userToUpdate.setEmail(user.getEmail());
        }
        log.info("Пользователь:{} обновлен", user);
        return userToUpdate;
    }

    @Override
    public void deleteById(Long id) {
        if (!users.containsKey(id)) {
            log.debug("Пользователь с id:{} не найден", id);
            throw new UserNotFoundException(id);
        }
        users.remove(id);
        log.info("Пользователь с id:{} удален", id);
    }

    private void checkEmail(User user) {
        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new UserDuplicateEmailException(user);
        }
    }
}
