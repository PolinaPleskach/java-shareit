package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId, @RequestBody ItemDto itemDto) {
        log.info("Пришел POST запрос /items с телом: {}", itemDto);
        ItemDto createdItem = itemService.create(userId, itemDto);
        log.info("Отправлен ответ /items с телом: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{item-id}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable("item-id") Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Пришел PATCH запрос /items/{} с телом: {} от пользователя с ID: {}", itemId, itemDto, userId);
        ItemDto updatedItem = itemService.update(userId, itemId, itemDto);
        log.info("Отправлен ответ /items/{} с телом: {}", itemId, updatedItem);
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable @NotNull Long itemId) {
        log.info("Пришел DELETE запрос /items/{}", itemId);
        itemService.delete(itemId);
        log.info("Отправлен ответ /items/{}", itemId);
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
        log.info("Пришел GET запрос /items от пользователя с ID: {}", userId);
        List<ItemFullDto> items = itemService.getUserItems(userId);
        log.info("Отправлен ответ /items с телом: {}", items);
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        log.info("Пришел GET запрос /items/search с параметром text: {}", text);
        List<ItemDto> items = itemService.searchItemsByText(text);
        log.info("Отправлен ответ /items/search с телом: {}", items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentFullDto addComments(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @Valid @RequestBody CommentDto commentDto,
                                      @PathVariable Long itemId) {
        log.info("Пришел POST запрос /items/{}/comment с телом: {} от пользователя с ID: {}", itemId, commentDto, userId);
        CommentFullDto comment = itemService.addComments(userId, commentDto, itemId);
        log.info("Отправлен ответ /items/{}/comment с телом: {}", itemId, comment);
        return comment;
    }
}
