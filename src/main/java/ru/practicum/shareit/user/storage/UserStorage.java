package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User newUser);

    boolean delete(Long id);

    User findUser(Long userId);

    Collection<User> getUsers();

    boolean isUserWithEmailExist(String email);
}
