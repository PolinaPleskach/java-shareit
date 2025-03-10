package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Пришел POST запрос /users с телом: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("Отправлен ответ /users с телом: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        log.info("Пришел PATCH запрос /users/{} с телом: {}", userId, userDto);
        UserDto updatedUser = userService.update(userId, userDto);
        log.info("Отправлен ответ /users/{} с телом: {}", userId, updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Long userId) {
        log.info("Пришел DELETE запрос /users/{}", userId);
        userService.delete(userId);
        log.info("Отправлен ответ /users/{}", userId);
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable("id") Long userId) {
        log.info("Пришел GET запрос /users/{}", userId);
        UserDto user = userService.findUser(userId);
        log.info("Отправлен ответ /users/{} с телом: {}", userId, user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Пришел GET запрос /users");
        List<User> users = userService.getUsers();
        log.info("Отправлен ответ /users с телом: {}", users);
        return users;
    }
}
