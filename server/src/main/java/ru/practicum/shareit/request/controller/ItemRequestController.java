package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestFullDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пришел POST запрос /requests с телом: {}", itemRequestDto);
        ItemRequestFullDto createdRequest = itemRequestService.create(userId, itemRequestDto);
        log.info("Отправлен ответ /requests с телом: {}", createdRequest);
        return createdRequest;
    }

    @GetMapping
    public List<ItemRequestSimpleDto> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Пришел GET запрос /requests от пользователя с ID: {}", userId);
        List<ItemRequestSimpleDto> requests = itemRequestService.getAllByUser(userId);
        log.info("Отправлен ответ /requests с телом: {}", requests);
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestSimpleDto> getAll(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Пришел GET запрос /requests/all от пользователя с ID: {}", userId);
        List<ItemRequestSimpleDto> requests = itemRequestService.getAll(userId);
        log.info("Отправлен ответ /requests/all с телом: {}", requests);
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestSimpleDto getById(@PathVariable Long requestId) {
        log.info("Пришел GET запрос /requests/{}", requestId);
        ItemRequestSimpleDto request = itemRequestService.getById(requestId);
        log.info("Отправлен ответ /requests/{} с телом: {}", requestId, request);
        return request;
    }
}
