package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    ItemStorage itemStorage;
    UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage,
                           @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(Long ownerId, NewItemRequest request) {
        log.debug("Создаем запись о вещи для пользователя с ID: {}", ownerId);
        User user = userStorage.findUser(ownerId);
        Item item = ItemMapper.mapToItem(ownerId, request);
        item = itemStorage.create(item);
        log.info("Создана запись о вещи: {}", item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, UpdateItemRequest request, Long ownerId) {
        log.debug("Обновление вещи с ID: {} для пользователя с ID: {}", itemId, ownerId);
        Item item = itemStorage.findItem(itemId);

        if (!item.getOwnerId().equals(ownerId)) {
            log.warn("Попытка обновления вещи с ID: {} не владельцем", itemId);
            throw new ItemOwnershipException("Редактировать данные вещи может только её владелец");
        }

        Item updatedItem = itemStorage.update(ItemMapper.updateItemFields(item, request));
        log.info("Обновлена вещь: {}", updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public boolean delete(Long ownerId, Long itemId) {
        log.debug("Удаление вещи с ID: {} для пользователя с ID: {}", itemId, ownerId);
        Item item = itemStorage.findItem(itemId);

        if (!item.getOwnerId().equals(ownerId)) {
            log.warn("Попытка удаления вещи с ID: {} не владельцем", itemId);
            throw new ItemOwnershipException("Удалять вещь может только её владелец");
        }

        boolean isDeleted = itemStorage.delete(itemId);
        log.info("Вещь с ID: {} удалена: {}", itemId, isDeleted);
        return isDeleted;
    }

    @Override
    public ItemDto findItem(Long ownerId, Long itemId) {
        log.debug("Поиск вещи с ID: {} для пользователя с ID: {}", itemId, ownerId);
        Item item = itemStorage.findItem(itemId);
        log.info("Найдена вещь: {}", item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchItemsByText(Long ownerId, String text) {
        log.debug("Поиск вещей по ключевым символам '{}' для пользователя с ID: {}", text, ownerId);
        if (StringUtils.isBlank(text)) {
            log.info("Ключевые символы отсутствуют, возвращаем пустой список");
            return new ArrayList<>();
        }

        Collection<Item> items = itemStorage.searchItemsByText(text.toLowerCase());
        log.info("Найдены вещи: {}", items);
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findAll(Long ownerId) {
        log.debug("Получение всех вещей для пользователя с ID: {}", ownerId);
        Collection<Item> items = itemStorage.getItems(ownerId);
        log.info("Найдены все вещи: {}", items);
        return items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
