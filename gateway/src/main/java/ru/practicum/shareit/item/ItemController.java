package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Пришел POST запрос /items с телом: {} от пользователя {}", itemDto, userId);
        ResponseEntity<Object> response = itemClient.create(userId, itemDto);
        log.info("Отправлен ответ POST /items с телом: {}", response.getBody());
        return response;
    }

    @GetMapping("/{item-id}")
    public ResponseEntity<Object> getByItemId(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                              @PathVariable("item-id") Long itemId) {
        log.info("Пришел GET запрос /items/{} от пользователя {}", itemId, userId);
        ResponseEntity<Object> response = itemClient.getById(userId, itemId);
        log.info("Отправлен ответ GET /items/{} с телом: {}", itemId, response.getBody());
        return response;
    }

    @PatchMapping("/{item-id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                         @PathVariable("item-id") Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Пришел PATCH запрос /items/{} с телом: {} от пользователя {}", itemId, itemDto, userId);
        ResponseEntity<Object> response = itemClient.update(userId, itemId, itemDto);
        log.info("Отправлен ответ PATCH /items/{} с телом: {}", itemId, response.getBody());
        return response;
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable @NotNull Long itemId) {
        log.info("Пришел DELETE запрос /items/{}", itemId);
        ResponseEntity<Object> response = itemClient.delete(itemId);
        log.info("Отправлен ответ DELETE /items/{} с телом: {}", itemId, response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_HEADER) @NotNull Long userId) {
        log.info("Пришел GET запрос /items от пользователя {}", userId);
        ResponseEntity<Object> response = itemClient.getOwnerItems(userId);
        log.info("Отправлен ответ GET /items с телом: {}", response.getBody());
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                                    @RequestParam @NotBlank String text) {
        log.info("Пришел GET запрос /items/search с текстом: {} от пользователя {}", text, userId);
        ResponseEntity<Object> response = itemClient.searchItemsByText(userId, text);
        log.info("Отправлен ответ GET /items/search с телом: {}", response.getBody());
        return response;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComments(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Valid @RequestBody CommentDto commentDto,
                                              @PathVariable @NotNull Long itemId) {
        log.info("Пришел POST запрос /items/{}/comment с телом: {} от пользователя {}", itemId, commentDto, userId);
        ResponseEntity<Object> response = itemClient.addComments(userId, itemId, commentDto);
        log.info("Отправлен ответ POST /items/{}/comment с телом: {}", itemId, response.getBody());
        return response;
    }
}
