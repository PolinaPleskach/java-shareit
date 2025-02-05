package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long ownerId, NewItemRequest request);

    ItemDto update(Long itemId, UpdateItemRequest request, Long ownerId);

    boolean delete(Long ownerId, Long itemId);

    ItemDto findItem(Long ownerId, Long itemId);

    Collection<ItemDto> searchItemsByText(Long ownerId, String text);

    Collection<ItemDto> findAll(Long ownerId);
}
