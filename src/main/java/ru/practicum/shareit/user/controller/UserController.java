package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
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
    public UserDto create(@Valid @RequestBody NewUserRequest user) {
        log.info("Пришел POST запрос /users с телом: {}", user);
        UserDto createdUser = userService.create(user);
        log.info("Отправлен ответ /users с телом: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @Valid @RequestBody UpdateUserRequest newUser) {
        log.info("Пришел PATCH запрос /users/{} с телом: {}", userId, newUser);
        UserDto updatedUser = userService.update(userId, newUser);
        log.info("Отправлен ответ /users/{} с телом: {}", userId, updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long userId) {
        log.info("Пришел DELETE запрос /users/{}", userId);
        boolean isDeleted = userService.delete(userId);
        log.info("Отправлен ответ /users/{} с результатом: {}", userId, isDeleted);
        return isDeleted;
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable("id") Long userId) {
        log.info("Пришел GET запрос /users/{}", userId);
        UserDto userDto = userService.findUser(userId);
        log.info("Отправлен ответ /users/{} с телом: {}", userId, userDto);
        return userDto;
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("Пришел GET запрос /users");
        Collection<UserDto> users = userService.getUsers();
        log.info("Отправлен ответ /users с телом: {}", users);
        return users;
    }
}
