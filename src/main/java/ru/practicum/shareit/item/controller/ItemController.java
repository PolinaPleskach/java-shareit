package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @RequestBody ItemDto item) {
        log.info("Пришел POST запрос /items с телом: {}", item);
        ItemDto createdItem = itemService.create(userId, item);
        log.info("Отправлен ответ /items с телом: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{item-id}")
    public ItemDto update(@PathVariable("item-id") Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Пришел PATCH запрос /items/{} с телом: {} от пользователя с ID: {}", itemId, itemDto, userId);
        ItemDto updatedItem = itemService.update(userId, itemId, itemDto);
        log.info("Отправлен ответ /items/{} с телом: {}", itemId, updatedItem);
        return updatedItem;
    }

    @GetMapping("/{item-id}")
    public ItemFullDto findItem(@PathVariable("item-id") Long itemId) {
        log.info("Пришел GET запрос /items/{}", itemId);
        ItemFullDto item = itemService.findItem(itemId);
        log.info("Отправлен ответ /items/{} с телом: {}", itemId, item);
        return item;
    }

    @GetMapping
    public List<ItemFullDto> getUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        log.info("Пришел GET запрос /items/search с параметром text: {} ", text);
        List<ItemDto> items = itemService.searchItemsByText(text);
        log.info("Отправлен ответ /items/search с телом: {}", items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentFullDto addComments(@RequestHeader(USER_ID_HEADER) Long userId, @Valid @RequestBody CommentDto commentDto,
                                      @PathVariable Long itemId) {
        return itemService.addComments(userId, commentDto, itemId);
    }
}
