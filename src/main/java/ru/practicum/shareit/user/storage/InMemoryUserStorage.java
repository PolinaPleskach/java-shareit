package ru.practicum.shareit.user.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component("InMemoryUserStorage")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private AtomicLong idCounter = new AtomicLong(1);
    private final Set emailUniqSet = new HashSet<>();

    @Override
    public User create(User user) {
        final String email = user.getEmail();
        if (emailUniqSet.contains(email)) {
            throw new DataAlreadyExistException("Email: " + email + " already exists");
        }
        long newId = idCounter.getAndIncrement();
        user.setId(newId);
        log.info("Создан новый пользователь с ID {}: {}", newId, user);
        users.put(newId, user);
        emailUniqSet.add(email);
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление данных пользователя с ID {}: {}", newUser.getId(), newUser);
        final String email = newUser.getEmail();
        users.computeIfPresent(newUser.getId(), (id, existingUser) -> {
            if (!email.equalsIgnoreCase(existingUser.getEmail())) {
                if (emailUniqSet.contains(email)) {
                    throw new DataAlreadyExistException("Email: " + email + " already exists");
                }
                emailUniqSet.remove(existingUser.getEmail());
                emailUniqSet.add(email);
            }
            return newUser;
        });
        return newUser;
    }

    @Override
    public boolean delete(Long userId) {
        User removedUser = users.remove(userId);
        if (removedUser != null) {
            log.info("Удален пользователь с ID {}: {}", userId, removedUser);
            emailUniqSet.remove(removedUser.getEmail());
            return true;
        } else {
            log.warn("Попытка удаления пользователя с ID {}, которого не существует", userId);
            return false;
        }
    }

    @Override
    public User findUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        log.info("Найден пользователь с ID {}: {}", userId, user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        log.info("Получение списка всех пользователей");
        return users.values();
    }

    @Override
    public boolean isUserWithEmailExist(String email) {
        boolean exists = users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
        log.info("Проверка существования пользователя с email {}: {}", email, exists);
        return exists;
    }
}
