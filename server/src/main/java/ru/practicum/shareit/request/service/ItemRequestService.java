package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestFullDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestSimpleDto> getAllByUser(Long userId);

    List<ItemRequestSimpleDto> getAll(Long userId);

    ItemRequestSimpleDto getById(Long requestId);
}