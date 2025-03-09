package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Пришел POST запрос /requests с телом: {} от пользователя {}", itemRequestDto, userId);
        ResponseEntity<Object> response = requestClient.create(userId, itemRequestDto);
        log.info("Отправлен ответ POST /requests с телом: {}", response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(USER_ID_HEADER) @NotNull Long userId) {
        log.info("Пришел GET запрос /requests от пользователя {}", userId);
        ResponseEntity<Object> response = requestClient.getAllByUser(userId);
        log.info("Отправлен ответ GET /requests с телом: {}", response.getBody());
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Пришел GET запрос /requests/all от пользователя {}", userId);
        ResponseEntity<Object> response = requestClient.getAll(userId);
        log.info("Отправлен ответ GET /requests/all с телом: {}", response.getBody());
        return response;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId) {
        log.info("Пришел GET запрос /requests/{}", requestId);
        ResponseEntity<Object> response = requestClient.getById(requestId);
        log.info("Отправлен ответ GET /requests/{} с телом: {}", requestId, response.getBody());
        return response;
    }
}
