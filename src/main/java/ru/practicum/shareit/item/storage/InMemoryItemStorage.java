package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryItemStorage")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InMemoryItemStorage implements ItemStorage {

    Map<Long, Item> items = new HashMap<>();
    AtomicLong idCounter = new AtomicLong(1);

    private boolean checkItem(Item item, String text) {
        return item.getAvailable() && (item.getName().toLowerCase().contains(text)
                || item.getDescription().toLowerCase().contains(text));
    }

    @Override
    public Item create(Item item) {
        long newId = idCounter.getAndIncrement();
        item.setId(newId);
        log.info("Создана новая вещь с ID {}: {}", newId, item);
        items.put(newId, item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        log.info("Обновление вещи с ID {}: {}", newItem.getId(), newItem);
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public boolean delete(Long itemId) {
        Item removedItem = items.remove(itemId);
        if (removedItem != null) {
            log.info("Удалена вещь с ID {}: {}", itemId, removedItem);
            return true;
        } else {
            log.warn("Попытка удаления вещи с ID {}, которая не существует", itemId);
            return false;
        }
    }

    @Override
    public Item findItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            log.warn("Вещь с ID {} не найдена", itemId);
            throw new NotFoundException(String.format("Вещь с ID %d не найдена", itemId));
        }
        log.info("Найдена вещь с ID {}: {}", itemId, item);
        return item;
    }

    @Override
    public Collection<Item> getItems(Long ownerId) {
        Collection<Item> ownerItems = items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
        log.info("Найдены вещи для пользователя с ID {}: {}", ownerId, ownerItems);
        return ownerItems;
    }

    @Override
    public Collection<Item> searchItemsByText(String text) {
        Collection<Item> matchingItems = items.values()
                .stream()
                .filter(item -> checkItem(item, text))
                .collect(Collectors.toList());
        log.info("Найдены вещи по тексту '{}': {}", text, matchingItems);
        return matchingItems;
    }
}
