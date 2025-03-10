package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Пришел POST запрос /users с телом: {}", userDto);
        ResponseEntity<Object> response = userClient.create(userDto);
        log.info("Отправлен ответ POST /users с телом: {}", response.getBody());
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Пришел PATCH запрос /users/{} с телом: {}", id, userDto);
        ResponseEntity<Object> response = userClient.update(id, userDto);
        log.info("Отправлен ответ PATCH /users/{} с телом: {}", id, response.getBody());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUser(@PathVariable Long id) {
        log.info("Пришел GET запрос /users/{}", id);
        ResponseEntity<Object> response = userClient.findUser(id);
        log.info("Отправлен ответ GET /users/{} с телом: {}", id, response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> findUsers() {
        log.info("Пришел GET запрос /users");
        ResponseEntity<Object> response = userClient.findUsers();
        log.info("Отправлен ответ GET /users с телом: {}", response.getBody());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Пришел DELETE запрос /users/{}", id);
        ResponseEntity<Object> response = userClient.delete(id);
        log.info("Отправлен ответ DELETE /users/{} с телом: {}", id, response.getBody());
        return response;
    }
}
