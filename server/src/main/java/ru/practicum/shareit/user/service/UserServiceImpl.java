package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidation userValidation;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Создание нового пользователя с данными: {}", userDto);
        userValidation.checkUserFields(userDto);
        User user = UserMapper.mapToUser(userDto);
        UserDto createdUser = UserMapper.mapToUserDto(userRepository.save(user));
        userRepository.flush();
        log.info("Создан новый пользователь: {}", user);
        return createdUser;
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (userId == null) {
            throw new ValidationException("id не указан");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        log.info("Удаление пользователя с ID {}", userId);
        if (userId == null) {
            throw new ValidationException("ID не указан");
        }
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto findUser(Long userId) {
        log.info("Поиск пользователя с ID {}", userId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не указан");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
        log.info("Найден пользователь: {}", user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        log.info("Найдены пользователи: {}", users);
        return users;
    }

    private void checkUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + id));
    }
}
