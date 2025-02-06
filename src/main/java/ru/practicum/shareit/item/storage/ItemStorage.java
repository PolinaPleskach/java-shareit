package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item request);

    Item update(Item request);

    boolean delete(Long itemId);

    Item findItem(Long itemId);

    Collection<Item> searchItemsByText(String text);

    Collection<Item> getItems(Long ownerId);
}
