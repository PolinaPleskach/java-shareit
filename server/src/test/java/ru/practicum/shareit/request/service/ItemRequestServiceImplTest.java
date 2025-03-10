package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {
    ItemRequestService itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserService userService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemMapper itemMapper;
    final Sort sort = Sort.by(Sort.Direction.DESC, "created");
    User user1;
    UserDto userDto;
    ItemRequestDto itemRequestDto1;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;
    Item item1;
    Item item2;

    @BeforeEach
    public void setUp() {
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        UserMapper userMapper = new UserMapper();
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Text");
        user1 = new User();
        user1.setName("Polina");
        user1.setEmail("polina@yandex.ru");
        user1.setId(1L);
        userRepository.save(user1);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Text");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setRequestor(user1);

        itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Text2");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setRequestor(user1);

        item1 = new Item();
        item1.setName("Vasilek");
        item1.setDescription("lovingKitty");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setItemRequest(itemRequest1);

        item2 = new Item();
        item2.setName("Vasilek");
        item2.setDescription("lovingKitty");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setItemRequest(itemRequest2);

        ItemSimpleDto i1 = new ItemSimpleDto(1L, "Vasilek", 1L);

    }

    @Test
    @DisplayName("ItemRequestService_create")
    void testCreate() {
        ItemRequestFullDto itemRequestFullDto = new ItemRequestFullDto();
        itemRequestFullDto.setId(itemRequest1.getId());
        itemRequestFullDto.setDescription(itemRequest1.getDescription());
        itemRequestFullDto.setRequestor(new UserDto(user1.getId(), user1.getName(), user1.getEmail()));
        when(userService.findUser(1L)).thenReturn(UserMapper.mapToUserDto(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest1);
        ItemRequestFullDto actualDto = itemRequestService.create(1L, itemRequestDto1);
        assertNotNull(actualDto);
        assertEquals(itemRequestFullDto.getDescription(), actualDto.getDescription());
        assertEquals(itemRequestFullDto.getRequestor().getName(), actualDto.getRequestor().getName());
        verify(userService).findUser(1L);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("ItemRequestService_getAllByUser")
    void testGetAllByUser() {
        when(itemRequestRepository.findAllByRequestorId(1L))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));
        final List<ItemRequestSimpleDto> itemRequestResponseDtoList = itemRequestService.getAllByUser(1L);
        assertEquals(2, itemRequestResponseDtoList.size());
    }

    @Test
    @DisplayName("ItemRequestService_getAll")
    void testGetAll() {
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(2L))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        final List<ItemRequestSimpleDto> itemRequestResponceDtoList = itemRequestService.getAll(2L);
        assertEquals(2, itemRequestResponceDtoList.size());
    }

    @Test
    @DisplayName("ItemRequestService_getByIdNotRequest")
    void testGetByIdNotRequest() {
        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(1L)
        );
    }

    @Test
    @DisplayName("ItemRequestService_getById")
    void testGetById() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByItemRequest(itemRequest1)).thenReturn(List.of(item1));
        final ItemRequestSimpleDto itemRequestResponceDto = itemRequestService.getById(1L);
        assertEquals("Text", itemRequestResponceDto.getDescription());
        assertEquals(1, itemRequestResponceDto.getItems().size());
    }
}