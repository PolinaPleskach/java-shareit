package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemValidation itemValidation;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("id не указан");
        }
        itemValidation.checkItemFields(itemDto);
        UserDto userDto = userService.findUser(userId);
        User user = UserMapper.mapToUser(userDto);
        Item item = ItemMapper.mapToItem(user, itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Нет такого запроса с id: " + itemDto.getRequestId()));
            item.setItemRequest(itemRequest);
        }
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new ValidationException("id не указан");
        }
        if (itemId == null) {
            throw new ValidationException("id не указан");
        }
        userService.findUser(userId);
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет предмета по id:" + itemId));
        checkOwner(userId, itemId);
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.mapToItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public void delete(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет предмета по id:" + itemId));
        itemRepository.delete(item);
    }

    @Override
    public ItemFullDto findItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет предмета по id:" + itemId));
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        Booking lastBooking = getLastBooking(bookings);
        Booking nextBooking = getNextBooking(bookings);
        return ItemMapper.mapToItemFullDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemFullDto> getUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemsId = items.stream().map(Item::getId).toList();
        List<Booking> bookings = bookingRepository.findAllByIdIn(itemsId);
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemsId);
        Map<Long, List<Booking>> bookingsMapByItemsId = new HashMap<>();
        Map<Long, List<Comment>> commentsMapByItemsID = new HashMap<>();
        for (Booking booking : bookings) {
            bookingsMapByItemsId.computeIfAbsent(booking.getItem().getId(), k -> new ArrayList<>()).add(booking);
        }
        for (Comment comment : comments) {
            commentsMapByItemsID.computeIfAbsent(comment.getItem().getId(), c -> new ArrayList<>()).add(comment);
        }
        List<ItemFullDto> itemInfoDto = new ArrayList<>();

        for (Item item : items) {
            List<Comment> listComments = commentsMapByItemsID.getOrDefault(item.getId(), new ArrayList<>());
            List<Booking> itemBookings = bookingsMapByItemsId.getOrDefault(item.getId(), new ArrayList<>());
            Booking lastBooking = getLastBooking(itemBookings);
            Booking nextBooking = getNextBooking(itemBookings);

            ItemFullDto infoDto = ItemMapper.mapToItemFullDto(item, lastBooking, nextBooking, listComments);
            itemInfoDto.add(infoDto);
        }
        return itemInfoDto;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentFullDto addComments(Long userId, CommentDto commentDto, Long itemId) {
        User user = UserMapper.mapToUser(userService.findUser(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета по id: " + itemId));
        List<Booking> bookings = bookingRepository.getALLByItemIdAndBookerIdAndStatusIsOrderByEndBookingDesc(itemId, userId, BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не бронировал вещь");
        }

        if (bookings.getFirst().getStartBooking().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.mapToComment(commentDto, user, item);
            return CommentMapper.mapToCommentFullDto(commentRepository.save(comment));
        } else {
            throw new ValidationException("Нет подходящей брони по параметрам");
        }
    }

    private Booking getLastBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStartBooking() != null)
                .max(Comparator.comparing(Booking::getStartBooking));
        return lastBooking.orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getEndBooking() != null)
                .max(Comparator.comparing(Booking::getEndBooking));
        return lastBooking.orElse(null);
    }

    private void checkOwner(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета по id: " + itemId));
        if (!userId.equals(item.getOwner().getId())) {
            throw new ItemOwnershipException("Только собственники предметов имеют доступ");
        }
    }
}