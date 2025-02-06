package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(NewUserRequest request) {
        log.info("Создание нового пользователя с данными: {}", request);

        if (userStorage.isUserWithEmailExist(request.getEmail())) {
            log.warn("Попытка создания пользователя с уже существующим email: {}", request.getEmail());
            throw new DuplicatedDataException(String.format("Этот E-mail \"%s\" уже используется", request.getEmail()));
        }

        User user = UserMapper.mapToUser(request);
        user = userStorage.create(user);
        log.info("Создан новый пользователь: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UpdateUserRequest request) {
        log.info("Обновление данных пользователя с ID {}: {}", userId, request);

        if (userId == null) {
            log.warn("ID пользователя не указан");
            throw new ValidationException("ID пользователя должен быть указан");
        }

        if (userStorage.isUserWithEmailExist(request.getEmail())) {
            log.warn("Попытка обновления пользователя с уже существующим email: {}", request.getEmail());
            throw new DuplicatedDataException(String.format("Этот E-mail \"%s\" уже используется", request.getEmail()));
        }

        User updatedUser = UserMapper.updateUserFields(userStorage.findUser(userId), request);
        updatedUser = userStorage.update(updatedUser);
        log.info("Обновлены данные пользователя: {}", updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public boolean delete(Long userId) {
        User user = userStorage.findUser(userId);
        log.info("Удаление пользователя с ID {}: {}", userId, user.getName());
        boolean isDeleted = userStorage.delete(userId);
        if (isDeleted) {
            log.info("Пользователь с ID {} удален", userId);
        } else {
            log.warn("Пользователь с ID {} не найден", userId);
        }
        return isDeleted;
    }

    @Override
    public UserDto findUser(Long userId) {
        log.info("Поиск пользователя с ID {}", userId);
        User user = userStorage.findUser(userId);
        log.info("Найден пользователь: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public Collection<UserDto> getUsers() {
        log.info("Получение списка всех пользователей");
        Collection<User> users = userStorage.getUsers();
        log.info("Найдены пользователи: {}", users);
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
