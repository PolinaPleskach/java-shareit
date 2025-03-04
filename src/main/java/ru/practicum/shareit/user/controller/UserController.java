package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    public void delete(@PathVariable("id") Long userId) {
        log.info("Пришел DELETE запрос /users/{}", userId);
        userService.delete(userId);
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable("id") Long userId) {
        log.info("Пришел GET запрос /users/{}", userId);
        UserDto userDto = userService.findUser(userId);
        log.info("Отправлен ответ /users/{} с телом: {}", userId, userDto);
        return userDto;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Пришел GET запрос /users");
        Collection<User> users = userService.getUsers();
        log.info("Отправлен ответ /users с телом: {}", users);
        return users;
    }
}
