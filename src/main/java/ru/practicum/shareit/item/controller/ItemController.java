package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

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
                          @Valid @RequestBody NewItemRequest item) {
        log.info("Пришел POST запрос /items с телом: {}", item);
        ItemDto createdItem = itemService.create(userId, item);
        log.info("Отправлен ответ /items с телом: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable("id") Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Пришел PATCH запрос /items/{} с телом: {} от пользователя с ID: {}", itemId, newItem, ownerId);
        ItemDto updatedItem = itemService.update(itemId, newItem, ownerId);
        log.info("Отправлен ответ /items/{} с телом: {}", itemId, updatedItem);
        return updatedItem;
    }

    @DeleteMapping("/{id}")
    public boolean delete(@RequestHeader(USER_ID_HEADER) Long ownerId,
                          @PathVariable("id") Long itemId) {
        log.info("Пришел DELETE запрос /items/{} от пользователя с ID: {}", itemId, ownerId);
        boolean deletedItem = itemService.delete(ownerId, itemId);
        log.info("Отправлен ответ /items/{} с результатом: {}", itemId, deletedItem);
        return deletedItem;
    }

    @GetMapping("/{id}")
    public ItemDto findItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                            @PathVariable("id") Long itemId) {
        log.info("Пришел GET запрос /items/{} от пользователя с ID: {}", itemId, ownerId);
        ItemDto item = itemService.findItem(ownerId, itemId);
        log.info("Отправлен ответ /items/{} с телом: {}", itemId, item);
        return item;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                 @RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Пришел GET запрос /items/search с параметром text: {} от пользователя с ID: {}", text, ownerId);
        Collection<ItemDto> items = itemService.searchItemsByText(ownerId, text);
        log.info("Отправлен ответ /items/search с телом: {}", items);
        return items;
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.info("Пришел GET запрос /items от пользователя с ID: {}", ownerId);
        Collection<ItemDto> items = itemService.findAll(ownerId);
        log.info("Отправлен ответ /items с телом: {}", items);
        return items;
    }
}
