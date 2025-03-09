package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user) {

        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
    }

    public static ItemRequestFullDto mapToItemRequestFullDto(ItemRequest itemRequest) {
        return new ItemRequestFullDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.mapToUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                Collections.emptyList()

        );
    }

    public static ItemRequestSimpleDto mapToItemRequestSimpleDto(ItemRequestFullDto itemRequestInfoDto) {
        return new ItemRequestSimpleDto(
                itemRequestInfoDto.getId(),
                itemRequestInfoDto.getDescription(),
                itemRequestInfoDto.getCreated(),
                itemRequestInfoDto.getItems()
        );
    }
}