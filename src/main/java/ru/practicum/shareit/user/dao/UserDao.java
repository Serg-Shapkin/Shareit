package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User create(User user);
    List<User> getAll();
    User getById(Long id);
    User update(User user);
    void deleteById(Long id);
}
