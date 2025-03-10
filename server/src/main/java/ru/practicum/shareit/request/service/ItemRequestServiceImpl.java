package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestFullDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.mapToUser(userService.findUser(userId));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, user);
        return ItemRequestMapper.mapToItemRequestFullDto(repository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestSimpleDto> getAllByUser(Long userId) {
        List<ItemRequestSimpleDto> list = new ArrayList<>();
        List<ItemRequest> requestsList = repository.findAllByRequestorId(userId);
        List<Item> itemslist = (itemRepository.findAll());
        for (ItemRequest request : requestsList) {
            List<ItemSimpleDto> sortList = itemslist.stream()
                    .filter(item -> item.getItemRequest() != null)
                    .filter(item -> item.getItemRequest().equals(request))
                    .map(ItemMapper::mapToItemSimpleDto)
                    .toList();
            ItemRequestFullDto itemRequestInfoDto = ItemRequestMapper.mapToItemRequestFullDto(request);
            ItemRequestSimpleDto itemRequestSimpleDto = ItemRequestMapper.mapToItemRequestSimpleDto(itemRequestInfoDto);
            itemRequestSimpleDto.setItems(sortList);
            list.add(itemRequestSimpleDto);
        }
        return list.stream().sorted(Comparator.comparing(ItemRequestSimpleDto::getCreated).reversed()).toList();
    }

    @Override
    public List<ItemRequestSimpleDto> getAll(Long userId) {
        List<ItemRequestSimpleDto> list = new ArrayList<>();
        List<ItemRequest> listRequestsFromDao = repository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        for (ItemRequest i : listRequestsFromDao) {
            ItemRequestSimpleDto itemRequestSimpleInfoDto = ItemRequestMapper.mapToItemRequestSimpleDto(ItemRequestMapper.mapToItemRequestFullDto(i));
            list.add(itemRequestSimpleInfoDto);
        }
        return list.stream().sorted(Comparator.comparing(ItemRequestSimpleDto::getCreated).reversed()).toList();
    }

    @Override
    public ItemRequestSimpleDto getById(Long requestId) {
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с id = {} нет." + requestId));
        ItemRequestSimpleDto itemRequestSimpleInfoDto = ItemRequestMapper.mapToItemRequestSimpleDto(ItemRequestMapper.mapToItemRequestFullDto(itemRequest));
        List<ItemSimpleDto> items = itemRepository.findAllByItemRequest(itemRequest)
                .stream().map(ItemMapper::mapToItemSimpleDto).toList();
        itemRequestSimpleInfoDto.setItems(items);
        return itemRequestSimpleInfoDto;
    }
}
