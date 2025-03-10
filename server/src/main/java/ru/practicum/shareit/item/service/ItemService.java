package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long ownerId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long itemId);

    ItemFullDto findItem(Long itemId);

    List<ItemDto> searchItemsByText(String text);

    List<ItemFullDto> getUserItems(Long userId);

    CommentFullDto addComments(Long userId, CommentDto commentDto, Long itemId);
}
